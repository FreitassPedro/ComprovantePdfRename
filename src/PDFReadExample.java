import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class PDFReadExample {
    public static void main(String args[]) throws IOException {

        pegarInfos();
        renameFile();

    }
    public static String dir = "C:\\Users\\CALL1\\Desktop\\doc\\";
   public static String abrirDocumento() {
        int x = 1;
        String path = dir + "comprovante (" + x + ").pdf";

        return path;
    }


    public static String pegarInfos() {
        StringBuilder sb = new StringBuilder("");

        String valor = "";
        String nomeDestinatario = "";
        String dataPagamento = "";
        try {
            //Create PdfReader instance.
            PDDocument document = PDDocument.load(new File(abrirDocumento()));
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
                    dataPagamento = fields[1].replace("/", ".");
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
        return "CP "
                + dataPagamento
                + " - "
                + nomeDestinatario
                + "  R$ "
                + valor
                + ".pdf";
    }

    public static void renameFile() throws IOException {
        String nomeAntigo = "C:\\Users\\CALL1\\Desktop\\doc\\comprovante.pdf";
        String nomeNovo = dir + pegarInfos();

        Path pathAntigo = Paths.get(nomeAntigo);
        Path pathNovo = Paths.get(nomeNovo);

        try {
            Files.move(pathAntigo, pathNovo, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Arquivo renomeado com sucesso.");
        } catch (Exception e) {
            System.out.println("Falha ao renomear o arquivo: " + e.getMessage());
        }
    }
}
