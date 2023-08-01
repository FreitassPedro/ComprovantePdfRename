package Types;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class fileInfos {
    public static String fileText(String path) throws IOException {
        try (PDDocument document = PDDocument.load(new File(path))) {
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
            document.close();
            return null;
        }
    }

    //APARTIR DO FILETEXT, COLETA QUAL O TIPO DO COMPROVANTE, POSSUINDO APENAS 2 TIPOS
    public static Integer fileType(String path) throws IOException {
        String text = fileText(path);
        if (text.startsWith("Ass")) {
            return 1;
        } else if (text.startsWith("Comprovante de Recebimento") || text.startsWith("Comprovante de Pagamento")) {
            return 3;
        } else {
            return 2;
        }
    }
}
