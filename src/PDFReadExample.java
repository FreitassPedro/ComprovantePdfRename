import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


public class PDFReadExample {
    public static void main(String[] args) {
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
                            String novoNomeFinal;
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
        List<String> lines = new ArrayList<>();
        String[] linhas = fileText(caminhoArquivo).split("\\r?\\n");

        String valor = "";
        String nomeDestinatario = "";
        String dataPagamento = "";
        String nomeDestinatarioAlt = "";

        for (String linha : linhas) {
            if (!linha.trim().isEmpty()) {
                lines.add(linha);
            }
        }
        String[] chavesDatas = {"Data do Pagamento", "Data da Transação"};
        String[] chavesPagamentos = {"Nome Destinatário", "Razão Social do Beneficiário", "Favorecido"};
        String[] chavesValores = {"Valor Total (R$)", "Valor Pago (R$)"};
        String[] chavesPagamentosAlt = {"Motivo Transferência", "Descrição de Pagamento"};

        for (String line : lines) {
            String[] fields = line.split(": ");
            String prefixo = fields[0];

            for (String chave : chavesDatas) {
                if (prefixo.equals(chave)) {
                    dataPagamento = fields[1].replace("/", ".");
                    break;
                }
            }
            for (String chave : chavesPagamentos) {
                for (String chaveAlt : chavesPagamentosAlt) {
                    if (prefixo.equals(chave)) {
                        nomeDestinatario = fields[1];
                        break;
                    }
                    if (prefixo.equals(chaveAlt)) {
                        nomeDestinatarioAlt = fields[1];
                    }
                }
                if (nomeDestinatario.equals("")) {
                    nomeDestinatario = nomeDestinatario;
                }
            }
            for (String chave : chavesValores) {
                if (prefixo.equals(chave)) {
                    valor = fields[1];
                    break;
                }
            }
        }
        return dataPagamento + " R$ " + valor + " " + nomeDestinatario + ".pdf";
    }


    //EXTRAI AS INFORMAÇÕES SE FOR O TIPO 2
    public static String pegarInfosTipo2(String caminhoArquivo) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] linhas = fileText(caminhoArquivo).split("\\r?\\n");

        String valor = "";
        String nomeDestinatario = "";
        String nomeDestinatarioAlt = "";
        String dataPagamento = "";
        String numeroDocumento = "";

        for (String linha : linhas) {
            if (!linha.trim().isEmpty()) {
                lines.add(linha);
            }
        }
        String[] chavesPagamentos = {"Valor Pago (R$):", "Valor (R$):", "Valor Total (R$):", "Valor Transferido (R$):", "Valor a Transferir (R$):"};
        String[] chavesDatas = {"Data do Pagamento:", "Data da Transação:", "Data Transferência:"};
        String[] chavesNomes = {"Razão Social do Beneficiário:", "Favorecido", "Empresa"};
        String[] chavesNomesAlt = {"Motivo Transferência", "Descrição de Pagamento"};
        for (String line : lines) {
            for (String chave : chavesPagamentos) {
                if (line.contains(chave)) {
                    int index = line.indexOf(chave);
                    valor = line.substring(0, index).trim();
                    break;
                }
            }

            for (String chave : chavesDatas) {
                if (line.contains(chave)) {
                    dataPagamento = line.substring(0, line.indexOf(chave));
                }
                dataPagamento = dataPagamento.replace("/", ".");

            }

            //CORRIGIR ESTA LINHA DE CÓDIGO, A DESCRIÇÃO ESTA PUXANDO ANTES
            for (String chave : chavesNomes) {
                for (String chaveAlt : chavesNomesAlt) {
                    if (line.contains(chave)) {
                        nomeDestinatario = line.substring(0, line.indexOf(chave));
                        break;
                    }
                    if (line.contains(chaveAlt)) {
                        nomeDestinatarioAlt = line.substring(0, line.indexOf(chaveAlt));
                    }
                }


                if (line.contains("Número do Documento:")) {
                    numeroDocumento = line.substring(0, line.indexOf("Número do Documento:"));
                }
            }
            if (nomeDestinatario.equals("")) {
                nomeDestinatario = nomeDestinatarioAlt;
            }
        }
        if (!numeroDocumento.equals("")) {
            return dataPagamento + " R$ " + valor + " - DARF " + numeroDocumento + ".pdf";
        }
        return dataPagamento + " R$ " + valor + " " + nomeDestinatario + ".pdf";
    }
}
