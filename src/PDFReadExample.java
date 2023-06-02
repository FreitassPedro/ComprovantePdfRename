import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

/**
 * This class is used to read an existing
 * pdf file using iText jar.
 *
 * @author javawithease
 */
public class PDFReadExample {
    public static void main(String args[]) {

        LinkedHashMap<String, String> infos = new LinkedHashMap<>();
        StringBuilder sb = new StringBuilder("");
        String valor = "";
        String nomeDestinatario = "";
        String dataPagamento = "";
        try {
            //Create PdfReader instance.
            PDDocument document = PDDocument.load(new File("C:\\Users\\CALL1\\Desktop\\doc\\comprovante (1).pdf"));

            String text = "";
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                text = stripper.getText(document);
            }
            sb.append(text);
            String[] linhas = sb.toString().split("\\r?\\n");

            for (int i = 6; i < linhas.length; i++) {
                String data1 = linhas[i];
                String[] fields = data1.split(": ");
                String prefixo = fields[0];


                if (prefixo.equals("Data do Pagamento")) {
                    dataPagamento = fields[1];
                }
                if (prefixo.equals("Nome Destinatário")) {
                    nomeDestinatario = fields[1];
                }
                if (prefixo.equals("Valor Total (R$)")) {
                    valor = fields[1];
                }
            }

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Comprovante "
                + dataPagamento
                + " - "
                + nomeDestinatario
                + " "
                + valor);

    }

}