/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author padoura
 */
public class FilenameEditor {
    
    /**
    * at most 120 chars allowed, assuming 12 digits in total for turn number 
    * and game id, default save folder for Civ 5, and a few more spare characters
    */
    public static String shortenName(String filename){
        if (filename.length() > 120) 
            return filename.substring(0, 226);
        return filename;
    }
    
    public static String replaceGroup(String regex, String source, int groupToReplace, String replacement) {
        Matcher m = Pattern.compile(regex).matcher(source);
        StringBuilder strB = new StringBuilder(source);
        while (m.find())
            strB.replace(m.start(groupToReplace),m.end(groupToReplace),replacement);
        return strB.toString();
    }

}
