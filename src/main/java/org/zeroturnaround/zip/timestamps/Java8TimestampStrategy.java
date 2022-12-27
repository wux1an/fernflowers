package org.zeroturnaround.zip.timestamps;

import java.nio.file.attribute.FileTime;
import java.util.zip.ZipEntry;

/**
 * This strategy will call lastModifiedTime, creationTime and
 * lastAccessTime methods (added in Java 8). Don't use this class unless
 * you are running Java 8.
 * 
 * @since 1.9
 */
public class Java8TimestampStrategy implements TimestampStrategy {

  public void setTime(ZipEntry newInstance, ZipEntry oldInstance) {
    {
      FileTime time = oldInstance.getCreationTime();
      if (time != null) {
        newInstance.setCreationTime(time);
      }
    }
    {
      FileTime time = oldInstance.getLastModifiedTime();
      if (time != null) {
        newInstance.setLastModifiedTime(time);
      }
    }
    {
      FileTime time = oldInstance.getLastAccessTime();
      if (time != null) {
        newInstance.setLastAccessTime(time);
      }
    }
  }

}
