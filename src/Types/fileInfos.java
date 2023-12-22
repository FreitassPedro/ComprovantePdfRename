package Types;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static Types.Types.*;

public class fileInfos {
    public static String directory() {
        String username = System.getProperty("user.name");
        return "C:\\Users\\" + username + "\\Desktop\\Comprovantes\\";
    }

    private static String directoryRenomeados() {
        String username = System.getProperty("user.name");
        return "C:\\Users\\" + username + "\\Desktop\\Comprovantes\\Renomeados\\";
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

    public static void renameFiles() {
        File diretorio = new File(directory());
        File[] arquivos = diretorio.listFiles();
        short contadorComprovante = 0;
        if (arquivos != null) {
            for (int i = 0; i < arquivos.length; i++) {
                File arquivo = arquivos[i];
                if (arquivo.isFile()) {
                    String nomeTemporario = "comprovante " + (i + 1) + ".pdf";
                    File fileTemporario = new File(arquivo.getParent(), nomeTemporario);

                    boolean renomeadoComSucesso = arquivo.renameTo(fileTemporario);

                    if (renomeadoComSucesso) {
                        String nomeComInfos = directory() + nomeTemporario;
                        String dadosExtraidos = "";
                        try {
                            switch (fileInfos.fileType(fileTemporario.getPath())) {
                                case 1 -> dadosExtraidos = pegarInfosTipo1(nomeComInfos);
                                case 2 -> dadosExtraidos = pegarInfosTipo2(nomeComInfos);
                                case 3 -> dadosExtraidos = pegarInfosTipo3(nomeComInfos);
                            }

                            renomeacaoFinal(fileTemporario, dadosExtraidos, nomeTemporario);
                            contadorComprovante++;

                        } catch (Exception e) {
                            System.out.println("Algo deu errado" + nomeTemporario + ": " + e.getMessage());
                        }
                    } else {
                        System.out.println("Falha ao renomear o arquivo " + arquivo.getName());
                    }
                }
            }
        }
        System.out.println();
        abrirDiretorio(contadorComprovante);
    }

    //CLASSE QUE RENOMEIA O ARQUIVO COM AS INFORMAÇÕES EXTRAIDAS
    private static void renomeacaoFinal(File fileTemporario, String dadosExtraidos, String nomeTemporario) {
        File arquivoFinal = new File(directoryRenomeados(), dadosExtraidos + ".pdf");

        if (arquivoFinal.exists()) {
            // Se o arquivo final já existe, adicione ' (2)' ao nome
            arquivoFinal = new File(directoryRenomeados(), dadosExtraidos + " (2).pdf");
        }
        if (fileTemporario.renameTo(arquivoFinal)) {
            System.out.println("Arquivo " + nomeTemporario + " renomeado com sucesso para " + dadosExtraidos);
        } else {
            System.out.println("Erro ao renomear o arquivo " + nomeTemporario);
        }
    }

    //CRIA UM DIRETÓRIO NO WINDOWS DO USUÁRIO PARA SALVAR O TXT
    public static void criadorRepositorio() {
        Path directoryPath = Paths.get(directoryRenomeados());

        // Verifica se o diretório já existe
        if (Files.exists(directoryPath)) {
            System.out.println("Estou pronto, preparando os motores...");
            System.out.println("Vamos lá:");
            System.out.println();
        } else {
            try {
                // Cria o diretório
                Files.createDirectories(directoryPath);
                System.out.println("Diretório criado:  " + directoryPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void abrirDiretorio(short contadorComprovante) {
        // Verifica se o Desktop é suportado
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                // Abre o Explorer na pasta especificada
                if (contadorComprovante != 0) {
                    System.out.println(contadorComprovante + " Comprovantes foram renomeados! Operação concluída.");
                    desktop.open(new File(directoryRenomeados()));
                } else {
                    desktop.open(new File(directory()));
                    System.out.println("Nenhum compovante foi encontrado. Por favor, adicione nesta pasta.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Desktop não é suportado.");
        }
    }
}
