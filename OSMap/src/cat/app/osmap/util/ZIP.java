package cat.app.osmap.util;

import android.util.Log; 

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileOutputStream; 
import java.util.zip.ZipEntry; 
import java.util.zip.ZipInputStream; 
import java.util.zip.ZipOutputStream;
 
/** 
 * 
 * @author wsun 
 */ 
public class ZIP { 
	private static final int BUFFER = 80000;
  private String _zipFile; 
  private String _location; 
 
  public ZIP(String zipFile, String location) {
    _zipFile = zipFile; 
    _location = location; 
 
    dirChecker(""); 
  } 
 
  public void unzip() { 
	  unzip(_zipFile,_location);
  } 
 
  private void dirChecker(String dir) { 
    File f = new File(_location + dir);
 
    if(!f.isDirectory()) {
      f.mkdirs(); 
    } 
  }
  public void zip(String[] _files, String zipFileName) {
      try {
          BufferedInputStream origin = null;
          FileOutputStream dest = new FileOutputStream(zipFileName);
          ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
              dest));
          byte data[] = new byte[BUFFER];

          for (int i = 0; i < _files.length; i++) {
              Log.v("Compress", "Adding: " + _files[i]);
              FileInputStream fi = new FileInputStream(_files[i]);
              origin = new BufferedInputStream(fi, BUFFER);

              ZipEntry entry = new ZipEntry(_files[i].substring(_files[i]
                  .lastIndexOf("/") + 1));
              out.putNextEntry(entry);
              int count;

              while ((count = origin.read(data, 0, BUFFER)) != -1) {
                  out.write(data, 0, count);
              }
              origin.close();
          }
          out.close();
      } catch (Exception e) {
          e.printStackTrace();
      }
  }
  public void unzip(String _zipFile, String _targetLocation) {
      // create target location folder if not exist
      dirChecker(_targetLocation);
      try {
          FileInputStream fin = new FileInputStream(_zipFile);
          ZipInputStream zin = new ZipInputStream(fin);
          ZipEntry ze = null;
          while ((ze = zin.getNextEntry()) != null) {
              // create dir if required while unzipping
              if (ze.isDirectory()) {
                  dirChecker(ze.getName());
              } else {
                  FileOutputStream fout = new FileOutputStream(
                  _targetLocation + "/" + ze.getName());
                  BufferedOutputStream bufout = new BufferedOutputStream(fout);
                  byte[] buffer = new byte[1024];
                  int read = 0;
                  while ((read = zin.read(buffer)) != -1) {
                      bufout.write(buffer, 0, read);
                  }

                  zin.closeEntry();
                  bufout.close();
                  fout.close();
              }
          }
          zin.close();
      } catch (Exception e) {
          System.out.println(e);
      }
  }
} 