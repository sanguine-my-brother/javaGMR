/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.githubupdater.domain;

import java.util.Objects;
import org.joda.time.DateTime;

/**
 *
 * @author danny_000
 */
public class Asset {
    String url;
    String browser_download_url;
    int id;
    String name;
    String label;
    String state;
    String content_type;
    long size;
    long download_count;
    DateTime created_at;
    DateTime updated_at;
    GithubUser uploader;

    public Asset() {
    }

    public Asset(String url, String browser_download_url, int id, String name, String label, String state, String content_type, long size, long download_count, DateTime created_at, DateTime updated_at, GithubUser uploader) {
        this.url = url;
        this.browser_download_url = browser_download_url;
        this.id = id;
        this.name = name;
        this.label = label;
        this.state = state;
        this.content_type = content_type;
        this.size = size;
        this.download_count = download_count;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.uploader = uploader;
    }

    public String getState() {
        return state;
    }



    public String getUrl() {
        return url;
    }

    public String getBrowser_download_url() {
        return browser_download_url;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public String getContent_type() {
        return content_type;
    }

    public long getSize() {
        return size;
    }

    public long getDownload_count() {
        return download_count;
    }

    public DateTime getCreated_at() {
        return created_at;
    }

    public DateTime getUpdated_at() {
        return updated_at;
    }

    public GithubUser getUploader() {
        return uploader;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.content_type);
        hash = 79 * hash + (int) (this.download_count ^ (this.download_count >>> 32));
        hash = 79 * hash + Objects.hashCode(this.created_at);
        hash = 79 * hash + Objects.hashCode(this.updated_at);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Asset other = (Asset) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.content_type, other.content_type)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString(){
        return this.name;
    }

    
    
    
    
    
}
