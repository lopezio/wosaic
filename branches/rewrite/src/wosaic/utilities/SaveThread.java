package wosaic.utilities;
import java.awt.image.BufferedImage;
import java.io.File;


public class SaveThread implements Runnable {

	Mosaic mos;
	Status statusObject;
	File file;
	
	public SaveThread(Mosaic m, Status s, File f) {
		file = f;
		mos = m;
		statusObject = s;
	}
	
	public void run() {
		try {
            final BufferedImage img = mos.createImage();
            String path = file.getAbsolutePath();
            final String lcasePath = path.toLowerCase();

            if (!lcasePath.contains(".jpg")
                    && !lcasePath.contains(".jpeg")) {
                path += ".jpg";
            }

            mos.save(img, path, "JPEG");
            statusObject.setStatus("Save Complete!");

        } catch (final Throwable e) {
            System.out.println("Save failed: ");
            System.out.println(e);
            statusObject.setIndeterminate(false);
            statusObject.setProgress(0);
            statusObject.setStatus("Save Failed!");
        }

        statusObject.setIndeterminate(false);

	}
	
}
