package photoviewer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;


/**
 * Basic image viewer that displays the currently displayed image from the config file provided by joining the
 * "sourcePath" and "currentFile" properties and making the full path to the image file.
 * These properties are polled several times per second and the viewer will update itself if the image file
 * changes.
 * This should be run with one argument, which is the full path to the config file.
 * @see PhotoPanel
 * @author Joshua Newman, NChain Ltd, January 2018
 */
public class CommandFrame extends JFrame {


    public static void main(String[] args) {
        new CommandFrame(args[0]);
    }

    public CommandFrame(String configFile) {

        try {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            getContentPane().setLayout(new BorderLayout());

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int heightUnit = screenSize.height / 50;
            int widthUnit = screenSize.width / 50;

            this.setMinimumSize(new Dimension(widthUnit * 50, heightUnit * 35));
            this.setPreferredSize(new Dimension(widthUnit * 50, heightUnit * 35));
            this.setLocation(new Point(0, heightUnit * 6));
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setVisible(true);

            pack();

            String oldPhoto = null;
            String newPhoto;
            do {
                newPhoto = getCurrentPhoto(configFile);
                if (newPhoto != null && !newPhoto.equals(oldPhoto)) {
                    updatePhoto(newPhoto);
                    oldPhoto = newPhoto;
                }
                Thread.sleep(200);
            } while (newPhoto != null);

            System.out.println("No file.  Exiting");
            System.exit(0);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String getCurrentPhoto(String configFile) {
        if(!new File(configFile).exists()) {
            return null;
        }
        Properties properties = new Properties();
        try(Reader reader = new FileReader(configFile)) {
            properties.load(reader);
            String currentImage = properties.getProperty("currentFile");
            if(currentImage == null) {
                return null;
            }
            return properties.get("sourcePath") + File.separator + currentImage;
        }
        catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    private void updatePhoto(String newPhoto) throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> {
            this.add(new PhotoPanel(new File(newPhoto)));
            pack();
        });
    }
}