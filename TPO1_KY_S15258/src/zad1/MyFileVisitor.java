package zad1;

/**
 * Created by yaroslavkohun on 3/12/18.
 */

import static java.nio.file.FileVisitResult.*;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.APPEND;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.EnumSet;
import java.nio.channels.*;
import java.nio.charset.Charset;

public class MyFileVisitor extends SimpleFileVisitor<Path> {
    private FileChannel outputFileChannel;
    private ByteBuffer  dataByteBuffer;

    public MyFileVisitor(Path dataFile) throws IOException {
        this.outputFileChannel = FileChannel.open(dataFile, EnumSet.of(CREATE, APPEND));
    }

    private void recodeToUTF8(FileChannel inputFileChannel, int dataByteBufferSize){
        dataByteBuffer = ByteBuffer.allocate(dataByteBufferSize +1);
        dataByteBuffer.clear();

        try {
            inputFileChannel.read(dataByteBuffer);
            dataByteBuffer.flip();
            ByteBuffer encodeBuffer = Charset.forName("UTF-8")
                    .encode(Charset.forName("Cp1250")
                            .decode(dataByteBuffer));

            while(encodeBuffer.hasRemaining())
                this.outputFileChannel.write(encodeBuffer);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public FileVisitResult visitFile(Path file_path, BasicFileAttributes attr) {
        try{
            this.recodeToUTF8(FileChannel.open(file_path), (int)attr.size());
        }catch(IOException ex){
            ex.printStackTrace();
        }
        return CONTINUE;
    }
}

