import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ImportadorCSV {

    public static class ResultadoImportacao {
        private final List<Aluno> alunosImportados = new ArrayList<>();
        private final List<String> erros = new ArrayList<>();
        private int importados = 0;
        private int rejeitados = 0;

        public List<Aluno> getAlunosImportados() {
            return alunosImportados;
        }

        public List<String> getErros() {
            return erros;
        }

        public int getImportados() {
            return importados;
        }

        public int getRejeitados() {
            return rejeitados;
        }

        public String getResumo() {
            return importados + " importados, " + rejeitados + " rejeitados";
        }
    }

    public static ResultadoImportacao importarAlunos(String caminhoArquivo) {
        return importarAlunos(caminhoArquivo, new AlunoDAO());
    }

    public static ResultadoImportacao importarAlunos(String caminhoArquivo, AlunoDAO alunoDAO) {
        if (caminhoArquivo == null || caminhoArquivo.trim().isEmpty()) {
            throw new ArquivoInvalidoException("Caminho do arquivo CSV não informado.");
        }

        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists() || !arquivo.isFile()) {
            throw new ArquivoInvalidoException("Arquivo CSV não encontrado: " + caminhoArquivo);
        }

        ResultadoImportacao resultado = new ResultadoImportacao();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8))) {

            String linha;
            int numeroLinha = 0;
            boolean leuAlgumaLinha = false;

            while ((linha = reader.readLine()) != null) {
                numeroLinha++;
                if (linha.trim().isEmpty()) {
                    continue;
                }

                leuAlgumaLinha = true;

                // Ignora cabeçalho se a primeira linha começar com "id" ou "nome"
                String inicio = linha.trim().toLowerCase();
                if (numeroLinha == 1 && (inicio.startsWith("id") || inicio.startsWith("nome"))) {
                    continue;
                }

                try {
                    Aluno aluno = parseLinhaAluno(linha);
                    alunoDAO.salvar(aluno);
                    resultado.alunosImportados.add(aluno);
                    resultado.importados++;
                } catch (IllegalArgumentException | RegistroDuplicadoException e) {
                    resultado.rejeitados++;
                    resultado.erros.add("Linha " + numeroLinha + " rejeitada: " + e.getMessage());
                }
            }

            if (!leuAlgumaLinha) {
                throw new ArquivoInvalidoException("Arquivo CSV está vazio: " + caminhoArquivo);
            }

        } catch (IOException e) {
            throw new ArquivoInvalidoException("Erro ao ler o arquivo CSV: " + e.getMessage());
        }

        return resultado;
    }

    private static Aluno parseLinhaAluno(String linha) {
        String separador = linha.contains(";") ? ";" : ",";
        String[] campos = linha.split(separador, -1);

        if (campos.length < 8) {
            throw new IllegalArgumentException("quantidade de colunas insuficiente (esperado 8 campos).");
        }

        for (int i = 0; i < 8; i++) {
            campos[i] = campos[i].trim();
            if (campos[i].isEmpty()) {
                throw new IllegalArgumentException("campo obrigatório em branco.");
            }
        }

        int id;
        int matricula;
        int numero;
        try {
            id = Integer.parseInt(campos[0]);
            matricula = Integer.parseInt(campos[2]);
            numero = Integer.parseInt(campos[5]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("id, matrícula ou número do endereço inválido.");
        }

        String nome = campos[1];
        String email = campos[3];
        String rua = campos[4];
        String cidade = campos[6];
        String uf = campos[7];

        return new Aluno(id, nome, matricula, email, rua, numero, cidade, uf);
    }
}
