package nl.redrock.maven.plugins.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Some file utils for zipping, unzipping and deleting
 *
 * @author Hugo Hendriks
 */
public class FileUtils {

    private static final Logger logger = Logger.getLogger(FileUtils.class.getName());

    /**
     * Unzip it
     *
     * @param aZipFile zip file including the directory
     * @param aOutputFolder the temp output folder
     */
    public static void unZipIt(String aZipFile, File aOutputFolder) {

        byte[] buffer = new byte[1024];

        try {

            //create output directory if not exists
            if (!aOutputFolder.exists()) {
                aOutputFolder.mkdir();
            }

            //get the zip file content
            ZipInputStream zis = new ZipInputStream(new FileInputStream(aZipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {

                String fileName = ze.getName();
                File newFile = new File(aOutputFolder + File.separator + fileName);

                logger.log(Level.FINE, "file unzip : " + newFile.getAbsoluteFile());

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            logger.log(Level.INFO, "Unzipping has finished");

        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage());
        }
    }

    /**
     * Delete a file but if the file is a directory it will delete all files
     * under the directory recursively
     *
     * @param file
     */
    public static void delete(File aFile) {

        if (aFile.isDirectory()) {

            //directory is empty, then delete it
            if (aFile.list().length == 0) {

                aFile.delete();
                logger.log(Level.FINE, "Directory is deleted : " + aFile.getAbsolutePath());

            } else {

                //list all the directory contents
                String files[] = aFile.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(aFile, temp);

                    //recursive delete
                    delete(fileDelete);
                }

                //check the directory again, if empty then delete it
                if (aFile.list().length == 0) {
                    aFile.delete();
                    logger.log(Level.FINE, "Directory is deleted : " + aFile.getAbsolutePath());
                }
            }

        } else {
            //if file, then delete it
            aFile.delete();
            logger.log(Level.FINE, "File is deleted : " + aFile.getAbsolutePath());
        }
    }

    /**
     * Traverse a directory and get all files, and add the file into fileList
     *
     * @param aNode file or directory
     */
    private static List<String> generateFileList(File aNode, String aPath) {

        List<String> result = new ArrayList<String>();

        //add files only
        if (aNode.isFile()) {
            result.add(generateZipEntry(aNode.getAbsoluteFile().toString(), aPath));
        }

        if (aNode.isDirectory()) {
            String[] subNote = aNode.list();
            for (String filename : subNote) {
                result.addAll(generateFileList(new File(aNode, filename), aPath));
            }
        }
        return result;
    }

    /**
     * Format the file path for zip
     *
     * @param file file path
     * @return Formatted file path
     */
    private static String generateZipEntry(String aFile, String aPath) {
        return aFile.substring(aPath.length() + 1, aFile.length());
    }

    /**
     * Zip it
     *
     * @param aZipFile output ZIP file location
     * @param aNode the file node to zip
     */
    public static void zipIt(String aZipFile, File aNode) {

        List<String> fileList = generateFileList(aNode, aNode.getAbsolutePath());

        byte[] buffer = new byte[1024];

        try {

            FileOutputStream fos = new FileOutputStream(aZipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            logger.log(Level.FINE, "Output to Zip : " + aZipFile);

            for (String file : fileList) {

                logger.log(Level.FINE, "File Added : " + file);
                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);

                FileInputStream in = new FileInputStream(aNode.getAbsolutePath() + File.separator + file);

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                in.close();
            }

            zos.closeEntry();
            //remember close it
            zos.close();

            logger.log(Level.INFO, "Zipping has finished");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
