/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.hyranasoftware.javagmr.util;

import java.io.File;
import java.util.Objects;
import org.joda.time.DateTime;

/**
 *
 * @author danny_000
 */
public class SaveFile extends File{
    
    DateTime lastTimeModified;
    long size;
    
    public SaveFile(String pathname) {
        super(pathname);
        size = this.length();
        lastTimeModified = new DateTime(this.lastModified());
    }

    public DateTime getLastTimeModified() {
        return lastTimeModified;
    }

    public long getSize() {
        return size;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.lastTimeModified);
        hash = 23 * hash + (int) (this.size ^ (this.size >>> 32));
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
        final SaveFile other = (SaveFile) obj;
        if (this.getName().equals(other.getName())) {
            return true;
        }
        return false;
    }
    
    
    
}
