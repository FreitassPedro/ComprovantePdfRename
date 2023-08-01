/* **************************************************************

Feito por Pedro Freitas
Como forma de estudos

Programa para renomear arquivos (comprovantes pdf) banco Sicredi
****************************************************************
 */


import Types.fileInfos;
import java.io.File;
import static Types.Types.*;

public class PDFReadExample {
    public static void main(String[] args) {
        renameFiles();
    }

    //DIRETÓRIO PADRÃO A SER USADO DURANTE EXECUÇÃO
    public static String directory() {
        String username = System.getProperty("user.name");
        return "C:\\Users\\" + username + "\\Desktop\\doc\\";
    }
    //NOME TEMPORARIO PARA NOVO ARQUIVO
    public static String novoNome = "comprovante";

    //RENOMEIA ARQUIVOS PARA NOME FINAL/TEMPORARIOS
    public static void renameFiles() {
        File diretorio = new File(directory());
        File[] arquivos = diretorio.listFiles();

        if (arquivos != null) {
            for (int i = 0; i < arquivos.length; i++) {
                File arquivo = arquivos[i];
                if (arquivo.isFile()) {
                    String novoNomeArquivo = novoNome + " " + i + ".pdf";
                    File novoArquivo = new File(arquivo.getParent(), novoNomeArquivo);

                    int contador = 2;
                    //TRANSFORMA NOME DO ARQUIVO EM "COMPROVANTE (i)"
                    while (novoArquivo.exists()) {
                        String nomeSemExtensao = novoNomeArquivo.substring(0, novoNomeArquivo.lastIndexOf('.'));
                        String extensao = novoNomeArquivo.substring(novoNomeArquivo.lastIndexOf('.'));
                        novoNomeArquivo = nomeSemExtensao + " (" + contador + ")" + extensao;
                        novoArquivo = new File(arquivo.getParent(), novoNomeArquivo);
                        contador++;
                    }

                    boolean renomeadoComSucesso = arquivo.renameTo(novoArquivo);

                    if (renomeadoComSucesso) {
                        String nomeComInfos = directory() + novoNomeArquivo;
                        String novoNomeFinal = "";
                        try {
                            Integer fileType = fileInfos.fileType(novoArquivo.getPath());
                            if (fileType == 1) {
                                novoNomeFinal = pegarInfosTipo1(nomeComInfos);
                            } else if (fileType == 2) {
                                novoNomeFinal = pegarInfosTipo2(nomeComInfos);
                            }
                            else if (fileType == 3) {
                                novoNomeFinal = pegarInfosTipo3(nomeComInfos);
                            }
                            File arquivoFinal = new File(directory(), novoNomeFinal);
                            novoArquivo.renameTo(arquivoFinal);
                            System.out.println("Arquivo " + novoNomeArquivo + " renomeado com sucesso para " + novoNomeFinal);
                        } catch (Exception e) {
                            System.out.println("Algo deu errado" + novoNomeArquivo + ": " + e.getMessage());
                        }
                    } else {
                        System.out.println("Falha ao renomear o arquivo " + arquivo.getName());
                    }
                }
            }
        }
    }
}
