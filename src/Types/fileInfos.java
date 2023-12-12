package Types;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

import static Types.Types.*;

public class fileInfos {
    public static String directory() {
        String username = System.getProperty("user.name");
        return "C:\\Users\\" + username + "\\Desktop\\docError\\teste\\";
        // return "C:\\Users\\" + username + "\\Desktop\\projetoExcel\\comprovantes\\";
    }
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
        assert text != null;
        if (text.startsWith("Ass")) {
            return 1;
        } else if (text.startsWith("Comprovante de Recebimento") || text.startsWith("Comprovante de Pagamento")) {
            return 3;
        } else {
            return 2;
        }
    }
    private static final String novoNome = "comprovante";
    public static void renameFiles() {
        File diretorio = new File(directory());
            File[] arquivos = diretorio.listFiles();
        int contadorComprovante = 0;
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
                        String nomeComInfos =  directory() + novoNomeArquivo;
                        String novoNomeFinal = "";
                        try {
                            int fileType = fileInfos.fileType(novoArquivo.getPath());
                            if (fileType == 1) {
                                novoNomeFinal = pegarInfosTipo1(nomeComInfos);
                            } else if (fileType == 2) {
                                novoNomeFinal = pegarInfosTipo2(nomeComInfos);
                            }
                            else if (fileType == 3) {
                                novoNomeFinal = pegarInfosTipo3(nomeComInfos);
                            }

                            /*Renomeia o arquivo com a String novoNomeFinal
                            Falha caso já existir algum arquivo com o mesmo nome, então adiciona "(2)"
                            */

                            File arquivoFinal = new File(directory(), novoNomeFinal + ".pdf");

                            if (arquivoFinal.exists()) {
                                // Se o arquivo final já existe, adicione ' (2)' ao nome
                                arquivoFinal = new File(directory(), novoNomeFinal + " (2).pdf");
                            }
                            if (novoArquivo.renameTo(arquivoFinal)) {
                                System.out.println("Arquivo " + novoNomeArquivo + " renomeado com sucesso para " + novoNomeFinal);
                                contadorComprovante++;
                            } else {
                                System.out.println("Erro ao renomear o arquivo " + novoNomeArquivo);
                            }
                        } catch (Exception e) {
                            System.out.println("Algo deu errado" + novoNomeArquivo + ": " + e.getMessage());
                        }
                    } else {
                        System.out.println("Falha ao renomear o arquivo " + arquivo.getName());
                    }
                }
            }
        }
        System.out.println();
        System.out.println(contadorComprovante + " Comprovantes foram renomeados! Operação concluída.");
    }
}
