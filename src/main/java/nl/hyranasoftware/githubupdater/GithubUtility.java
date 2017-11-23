/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.githubupdater;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.hyranasoftware.githubupdater.domain.Asset;
import nl.hyranasoftware.githubupdater.domain.Release;

/**
 *
 * @author danny_000
 */
public class GithubUtility {

    /*
    https://developer.github.com/v3/repos/releases/#get-the-latest-release
     */
    private String repoOwner;
    private String repoName;
    private String currentTagVersion;
    private String repoUrl;
    
    /**
     * Initializes the updater
     *
     * @param repoOwner The username/organisation of the repo owner
     * @param repoName The name of the repo
     * @param currentTagVersion The tag-name of the current version. Can be
     * null, if null there will always be a new release available.
     */
    public GithubUtility(String repoOwner, String repoName, String currentTagVersion) {
        this.repoOwner = repoOwner;
        this.repoName = repoName;
        if (currentTagVersion != null) {
            this.currentTagVersion = currentTagVersion;
        } else {
            this.currentTagVersion = "";
        }
        this.repoUrl = String.format("https://api.github.com/repos/%s/%s/releases", repoOwner, repoName);
    }

    /**
     * Returns a true if there is an update available.
     *
     * @return returns true if there is a newer release available
     * @throws UnirestException
     */
    public boolean checkForUpdates() throws UnirestException {
        Release release = getLatestRelease();
        
        if (release.getTag_name().equals(currentTagVersion)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Gets the latest release from the release page of your project.
     * Prereleases do not show up in the API when requesting for the latest
     * release. If there are no releases it returns a null
     *
     * @return Returns latest release, and if there is none it will return a
     * null
     * @throws UnirestException
     */
    public Release getLatestRelease() throws UnirestException {
        StringBuilder builder = new StringBuilder();
        String requestUrl = repoUrl + "/latest";
        String response = Unirest.get(requestUrl).asJson().getBody().toString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        try {
            Release release = mapper.readValue(response, Release.class);
            return release;
        } catch (IOException ex) {
            Logger.getLogger(GithubUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Downloads the asset from Github
     * @param asset The asset that should be downloaded
     * @return Returns the downloaded file
     * @throws UnirestException
     * @throws IOException 
     */
    public File downloadAsset(Asset asset) throws UnirestException, IOException {

        URL url = new URL(asset.getBrowser_download_url());
        HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
        long completeFileSize = httpConnection.getContentLength();
        httpConnection.setReadTimeout(15000);
        
        BufferedInputStream stream = new BufferedInputStream(httpConnection.getInputStream());
        byte[] buffer = new byte[8 * 1024];
        File file = new File(asset.getName());
        int bytesRead;
        BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(file));
        double downLoadFileSize = 0;
        while ((bytesRead = stream.read(buffer)) != -1) {
            downLoadFileSize = downLoadFileSize + bytesRead;
            outStream.write(buffer, 0, bytesRead);
            sendDownloadProgress(((double) downLoadFileSize / (double) completeFileSize));
        }
        outStream.close();
        Logger.getLogger(GithubUtility.class.getName()).log(Level.INFO, file.getAbsolutePath());
        return file;
    }

    /**
     * Gets all the releases from a Github Project
     * @return A list of all releases related to the github project
     * @throws UnirestException
     * @throws IOException 
     */
    public List<Release> getAllReleases() throws UnirestException, IOException {
        List<Release> releases = new ArrayList();
        StringBuilder builder = new StringBuilder();
        String requestUrl = repoUrl;
        String response = Unirest.get(requestUrl).asJson().getBody().toString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        releases = mapper.readValue(response, new TypeReference<List<Release>>() {
            });
        return releases;
    }

    /**
     * Downloads a specific asset to a certain location 
     * @param location specifiy the location where the file should be downloaded to
     * @param asset This asset will be downloaded
     * @return The downloaded file
     * @throws UnirestException
     * @throws IOException 
     */
    public File downloadAssetToSpecificLocation(Path location, Asset asset) throws UnirestException, IOException {
        File file = location.toFile();
        File newAsset = downloadAsset(asset);
        newAsset.renameTo(file);
        return file;
    }

    public boolean updateCurrentJar(File file, Asset asset) throws UnirestException, IOException {
        File newAsset = downloadAsset(asset);
        newAsset.renameTo(file);
        return true;
    }
    
    /**
     * This method must be overriden by your view.
     * It receives the download progress
     * @param percent 
     */
    public void sendDownloadProgress(double percent) {
                Logger.getLogger(GithubUtility.class.getName()).log(Level.INFO, percent + "");
        /*
        THIS MUST BE OVERRIDDEN BY YOUR VIEW
         */

    }

}
