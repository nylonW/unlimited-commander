package sample;

import java.io.File;
import java.text.SimpleDateFormat;

public class CommanderFile {

    private File file;
    private String name;
    private String date;
    private String size;

    CommanderFile(File file) {
        if (file.isDirectory()) {
            this.name = "\uD83D\uDCC1 " + file.getName();
        } else {
            this.name = "\uD83D\uDDC4 " + file.getName();
        }
        this.file = file;
    }

    CommanderFile(File file, String name) {
        this.name = name;
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return dateFormat.format(file.lastModified());
    }

    public String getSize() {
        return String.valueOf(file.getTotalSpace());
    }
}
