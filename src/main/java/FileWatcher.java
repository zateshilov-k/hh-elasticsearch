import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

public class FileWatcher {
    public static void main(String[] args) {
        Path testDir = Paths.get("/home/headon/ParseHHnew/test_dir/");

        try {
            System.out.println(testDir.toRealPath());
        } catch (IOException e) {
            System.err.println("Wrong path to watched directory (maybe dir doesn't exists)");
            e.printStackTrace();
        }

        try {
            WatchService watcher = testDir.getFileSystem().newWatchService();
            testDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            WatchKey watchKey = watcher.take();

            List<WatchEvent<?>> events = watchKey.pollEvents();
            for (WatchEvent event : events) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    System.out.println("Created: " + event.context().toString());
                }
                if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                    System.out.println("Delete: " + event.context().toString());
                }
                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    System.out.println("Modify: " + event.context().toString());
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }

    }
}
