package AutomatedTesting.OrangeHRM;

import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.File;

import static org.monte.media.AudioFormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class CustomScreenRecorder extends ScreenRecorder {
    public static ScreenRecorder screenRecorder;
    public String name;

    public CustomScreenRecorder(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat,
                                Format screenFormat, Format mouseFormat, Format audioFormat, File movieFolder, String name)
            throws Exception {
        super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
        this.name = name;
    }

    @Override
    protected File createMovieFile(Format fileFormat) {
        return new File(this.movieFolder, name + ".avi");
    }
}
