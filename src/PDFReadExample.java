/* **************************************************************

Feito por Pedro Freitas
Como forma de estudos

Programa para renomear arquivos (comprovantes pdf) banco Sicredi
****************************************************************
 */


import Types.fileInfos;
import java.io.File;
import static Types.Types.*;
import static Types.fileInfos.fileType;
import static Types.fileInfos.renameFiles;

public class PDFReadExample {
    public static void main(String[] args) {
        mensagemBoasVindas();
        renameFiles();
    }
    private static void mensagemBoasVindas() {
        System.out.println("Siga as intruções:");
        System.out.println("""
                Para começar, coloque todos arquivos de comprovante PDF na pasta PIX na área de trabalho!
                Se não tiver, basta apenas criar!
                Vamos começar!
                """);
    }
}
