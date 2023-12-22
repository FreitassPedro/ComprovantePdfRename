/* **************************************************************

Feito por Pedro Freitas
Como forma de estudos

Programa para renomear arquivos (comprovantes pdf) banco Sicredi
****************************************************************
 */
import static Types.fileInfos.criadorRepositorio;
import static Types.fileInfos.renameFiles;

public class PDFReadExample {
    public static void main(String[] args) {
        mensagemBoasVindas();
        criadorRepositorio();
        renameFiles();
    }

    private static void mensagemBoasVindas() {
        System.out.println("Siga as intruções:");
        System.out.println("""
                Para começar, coloque todos arquivos de comprovante PDF na pasta COMPROVANTES na área de trabalho!
                Se não tiver, criamos agora!
                """);
    }
}
