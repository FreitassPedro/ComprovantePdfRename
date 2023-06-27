import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


public class PDFReadExample {
    public static void main(String args[]) {
        renameFiles();
    }

    //DIRETÓRIO PADRÃO A SER USADO DURANTE EXECUÇÃO
    public static String directory = "C:\\Users\\CALL1\\Desktop\\doqui\\";
    //NOME TEMPORARIO PARA NOVO ARQUIVO
    public static String novoNome = "comprovante";
    //RENOMEIA ARQUIVOS PARA NOME FINAL/TEMPORARIOS
    public static void renameFiles() {
        File diretorio = new File(directory);
        File[] arquivos = diretorio.listFiles();

        if (arquivos != null) {
            for (int i = 0; i < arquivos.length; i++) {
                File arquivo = arquivos[i];
                if (arquivo.isFile()) {
                    String novoNomeArquivo = novoNome + " " + i + ".pdf";
                    File novoArquivo = new File(arquivo.getParent(), novoNomeArquivo);

                    int contador = 2;
                    while (novoArquivo.exists()) {
                        String nomeSemExtensao = novoNomeArquivo.substring(0, novoNomeArquivo.lastIndexOf('.'));
                        String extensao = novoNomeArquivo.substring(novoNomeArquivo.lastIndexOf('.'));
                        novoNomeArquivo = nomeSemExtensao + " (" + contador + ")" + extensao;
                        novoArquivo = new File(arquivo.getParent(), novoNomeArquivo);
                        contador++;
                    }

                    boolean renomeadoComSucesso = arquivo.renameTo(novoArquivo);

                    if (renomeadoComSucesso) {
                        String nomeComInfos = directory + novoNomeArquivo;
                        try {
                            Integer fileType = fileType(novoArquivo.getPath());
                            String novoNomeFinal = "";
                            if (fileType == 1) {
                                novoNomeFinal = pegarInfosTipo1(nomeComInfos);
                            } else {
                                novoNomeFinal = pegarInfosTipo2(nomeComInfos);
                            }
                            File arquivoFinal = new File(directory, novoNomeFinal);
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
    //TRANSFORMA O PDF EM TEXT E ARMAZENA NA STRING
    private static String fileText(String path) throws IOException {
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
    private static Integer fileType(String path) throws IOException {
        String text = fileText(path);
        if (text.length() > 0 && !text.startsWith("Ass")) {
            return 2;
        } else {
            return 1;
        }
    }
    //EXTRAI AS INFORMAÇÕES SE FOR O TIPO 1
    public static String pegarInfosTipo1(String caminhoArquivo) throws IOException {
        StringBuilder sb = new StringBuilder("");
        String valor = "";
        String nomeDestinatario = "";
        String dataPagamento = "";
        try {
            PDDocument document = PDDocument.load(new File(caminhoArquivo));
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);//;;/
            document.close();
            sb.append(text);
            String[] linhas = sb.toString().split("\\r?\\n");

            for (int i = 6; i < linhas.length; i++) {
                String data1 = linhas[i];
                String[] fields = data1.split(": ");
                String prefixo = fields[0];

                if (prefixo.equals("Data do Pagamento") || prefixo.equals("Data da Transação")) {
                    dataPagamento = fields[1].replace("/", ".");
                    //System.out.println(dataPagamento);
                }
                if (prefixo.equals("Nome Destinatário") || prefixo.equals("Razão Social do Beneficiário") || prefixo.equals("Favorecido")) {
                    nomeDestinatario = fields[1];
                    //System.out.println(nomeDestinatario);
                }
                if (prefixo.equals("Valor Total (R$)") || prefixo.equals("Valor Pago (R$)")) {
                    valor = fields[1];
                }
            }

        } catch (IOException e) {
            throw new IOException("Falha ao extrair informações do arquivo: " + e.getMessage());
        }

        return dataPagamento + " R$ " + valor + " " + nomeDestinatario + ".pdf";
    }
    //EXTRAI AS INFORMAÇÕES SE FOR O TIPO 1
    public static String pegarInfosTipo2(String caminhoArquivo) throws IOException {
        StringBuilder sb = new StringBuilder();
        List<String> lines = new ArrayList<>();
        sb.append(fileText(caminhoArquivo));
        String[] linhas = sb.toString().split("\\r?\\n");

        String valor = "";
        String nomeDestinatario = "";
        String dataPagamento = "";
        String numeroDocumento = "";

        for (String linha : linhas) {
            if (!linha.trim().isEmpty()) {
                lines.add(linha);
            }
        }
        for (String line : lines) {
            if (line.contains("Valor Pago (R$):") || line.contains("Valor (R$):") || line.contains("Valor Total (R$):") || line.contains("Valor Transferido (R$):")) {
                if (line.contains("Valor Pago (R$):")) {
                    int index = line.indexOf("Valor Pago (R$):");
                    valor = line.substring(0, index).trim();
                } else if (line.contains("Valor (R$):")) {
                    int index = line.indexOf("Valor (R$):");
                    valor = line.substring(0, index).trim();
                } else if (line.contains("Valor Transferido (R$):")) {
                    int index = line.indexOf("Valor Transferido (R$):");
                    valor = line.substring(0, index).trim();
                } else {
                    int index = line.indexOf("Valor Total (R$):");
                    valor = line.substring(0, index).trim();
                }

            } else if (line.contains("Data do Pagamento:") || line.contains("Data da Transação:") || line.contains("Data Transferência:")) {
                if (line.contains("Data do Pagamento:")) {
                    dataPagamento = line.substring(0, line.indexOf("Data do Pagamento"));
                } else if (line.contains("Data da Transação:")) {
                    dataPagamento = line.substring(0, line.indexOf("Data da Transação"));
                } else if (line.contains("Data Transferência:")) {
                    dataPagamento = line.substring(0, line.indexOf("Data Transferência"));
                }
                dataPagamento = dataPagamento.replace("/", ".");


            } else if (line.contains("Razão Social do Beneficiário:") || line.contains("Favorecido:")) {
                int index = 0;
                if (line.contains("Razão Social do Beneficiário:")) {
                    nomeDestinatario = line.substring(0, line.indexOf("Razão Social do Beneficiário:"));
                } else if (line.contains("Favorecido:")) {
                    nomeDestinatario = line.substring(0, line.indexOf("Favorecido"));
                    System.out.println(nomeDestinatario);
                }
            } else if (line.contains("Número do Documento:")) {
                numeroDocumento = line.substring(0, line.indexOf("Número do Documento:"));
            }

        }
        if (numeroDocumento != "") {
            return dataPagamento + " R$ " + valor + " - DARF" + numeroDocumento + ".pdf";
        }
        return dataPagamento + " R$ " + valor + " " + nomeDestinatario + ".pdf";
    }
}
