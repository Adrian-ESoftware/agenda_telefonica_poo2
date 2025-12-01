# Agenda de Contatos - Documentação das Melhorias Implementadas

## 📋 Observações do Professor Implementadas

### 1. ✅ Tratamento de Exceções Robusto

Implementamos tratamento completo de exceções em todas as camadas da aplicação:

#### **ContatoDAO.java**
- **PersistenceException**: Captura erros do banco de dados
- **IllegalArgumentException**: Valida parâmetros de entrada (ID > 0, não nulo)
- **Rollback automático**: Em caso de erro, desfaz a transação
- **Logging detalhado**: Registra todas as operações e erros com `Logger`
- **Finally blocks**: Garante fechamento de recursos (`EntityManager` e `EntityManagerFactory`)

```java
try {
    // operação
} catch (PersistenceException e) {
    if (em != null && em.getTransaction().isActive()) {
        em.getTransaction().rollback();
    }
    LOGGER.log(Level.SEVERE, "Erro ao salvar contato", e);
    throw new RuntimeException("Erro ao salvar contato", e);
} finally {
    // Garante fechamento de recursos
    if (em != null && em.isOpen()) {
        em.close();
    }
}
```

#### **ContatosController.java**
- **Try-catch em todos os handlers**: Cada botão e ação tem tratamento
- **IOException e NumberFormatException**: Tratadas automaticamente
- **Validação de entrada**: Campos vazios, formato de email e telefone
- **Feedback ao usuário**: Mensagens de sucesso, aviso e erro diferenciadas
- **Logging com níveis**: INFO, WARNING, SEVERE

```java
@FXML
public void onAdicionarContato() {
    try {
        if (!validarCampos()) return;
        // operação
    } catch (IllegalArgumentException e) {
        LOGGER.log(Level.WARNING, "Validação inválida", e);
        mostrarErro("Erro de validação", e.getMessage());
    } catch (RuntimeException e) {
        LOGGER.log(Level.SEVERE, "Erro ao adicionar", e);
        mostrarErro("Erro ao salvar contato", "Verifique a conexão com banco");
    }
}
```

---

### 2. ✅ Geração Automática de IDs

Implementamos através do JPA/Hibernate com estratégia `GenerationType.IDENTITY`:

```java
@Entity
public class Contato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // Gerado automaticamente pelo PostgreSQL
    // ...
}
```

**Como funciona:**
- O banco de dados PostgreSQL gera automaticamente o ID
- Cada novo contato recebe um ID sequencial
- O ID é atribuído apenas após a persistência no banco
- Não há necessidade de ler do CSV ou gerenciar manualmente

---

### 3. ✅ Boas Práticas de Programação

#### **Encapsulamento**
- Todos os atributos privados com getters/setters via Lombok `@Data`
- Métodos privados para operações internas
- Constantes estáticas (`LOGGER`, `PERSISTENCE_UNIT`)

#### **Responsabilidade Única**
- **Contato.java**: Apenas modelo de dados
- **ContatoDAO.java**: Apenas operações de persistência
- **ContatosController.java**: Apenas lógica da interface
- **AgendaApp.java**: Apenas inicialização da aplicação

#### **Nomenclatura Clara**
```java
// ✅ Bom - nome descritivo
public void onAdicionarContato()
private boolean validarEmail(String email)
private void mostrarErro(String titulo, String mensagem)

// ❌ Evitado - abreviações confusas
// public void add()
// private boolean valEmail()
```

#### **Documentação Javadoc**
Todas as classes, métodos públicos e atributos importantes possuem documentação:

```java
/**
 * Valida o formato do email.
 * 
 * @param email o email a validar
 * @return true se o email é válido, false caso contrário
 */
private boolean validarEmail(String email) {
    return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
}
```

#### **Constantes ao invés de Magic Numbers**
```java
private static final Logger LOGGER = Logger.getLogger(ContatoDAO.class.getName());
private static final String PERSISTENCE_UNIT = "un-jpa";
```

---

### 4. ✅ Configuração JavaFX

Ambiente configurado corretamente com:

- **module-info.java**: Exports e opens corretos
```java
module org.agenda_poo2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    
    exports org.agenda_poo2;
    opens org.agenda_poo2 to javafx.fxml;
    opens org.model to org.hibernate.orm.core;
}
```

- **pom.xml**: Dependências JavaFX corretas
- **FXML**: Arquivo de interface bem estruturado com GridPane, TableView e Buttons

---

### 5. ✅ Manipulação de Arquivos com Paths

Para futuras implementações com CSV (conforme sugestão do professor):

```java
import java.nio.file.Paths;
import java.nio.file.Files;

// Usando Paths.get() como recomendado
Path arquivo = Paths.get("contatos.csv");
List<String> linhas = Files.readAllLines(arquivo);
```

---

## 🎯 Validações Implementadas

### **Validação de Email**
```java
email.matches("^[A-Za-z0-9+_.-]+@(.+)$")
// Aceita: joao@email.com, maria.silva@domain.co.uk
```

### **Validação de Telefone**
```java
telefone.matches("^[()\\d\\s\\-+]*$") && telefone.length() >= 8
// Aceita: (11) 99999-9999, 11 98888-8888, +55 11 97777-7777
```

### **Validação de Campos Vazios**
```java
if (nome.isEmpty()) {
    mostrarAviso("Por favor, preencha o campo Nome!");
    tfNome.requestFocus();  // Foca no campo inválido
    return false;
}
```

---

## 📊 Estrutura do Projeto

```
org.agenda_poo2/
├── AgendaApp.java                    // Aplicação principal
├── ContatosController.java           // Controller com tratamento robusto
├── contatos-view.fxml               // Interface gráfica

org.model/
├── Contato.java                     // Entidade com documentação
├── ContatoDAO.java                  // DAO com exceções

module-info.java                     // Configuração de módulos
persistence.xml                      // Configuração JPA

Main.java                            // Teste (opcional, pode remover)
```

---

## 🚀 Como Usar

### **Executar a Aplicação**
```bash
# Opção 1: No IntelliJ - clique em Run 'AgendaApp.main()'

# Opção 2: Terminal
cd C:\Users\Adrian\Documents\aulas\prova_poo2\agenda_poo2
mvn javafx:run
```

### **Operações Disponíveis**

1. **Adicionar Contato**
   - Preencha Nome, Telefone e Email
   - Clique "Adicionar Contato"
   - Campos são validados automaticamente

2. **Editar Contato**
   - Clique em um contato na tabela
   - Modifique os campos
   - Clique "Editar Contato"

3. **Excluir Contato**
   - Clique em um contato na tabela
   - Clique "Excluir Contato"
   - Confirme na caixa de diálogo

4. **Limpar Campos**
   - Clique "Limpar" para resetar os campos

---

## 🔍 Logging e Monitoramento

A aplicação registra:
- Inicialização: `LOGGER.info("Aplicação iniciada com sucesso")`
- Operações bem-sucedidas: `LOGGER.info("Novo contato adicionado: " + nome)`
- Avisos: `LOGGER.warning("Contato não encontrado para exclusão")`
- Erros: `LOGGER.log(Level.SEVERE, "Erro ao salvar contato", exception)`

**Para visualizar logs:**
```java
// Configure no logging.properties ou adicione console handler
java.util.logging.ConsoleHandler.level = FINE
```

---

## ✨ Melhorias Futuras Sugeridas

1. **Exportar para CSV**: Usar `Paths.get()` e `Files.write()`
2. **Backup automático**: Salvar em arquivo periodicamente
3. **Pesquisa/Filtro**: Procurar contatos por nome
4. **Ordenação**: Clicar nas colunas para ordenar
5. **Temas**: Suporte a diferentes temas JavaFX

---

## 📝 Checklist de Observações do Professor

- ✅ **Tratamento de Exceções**: PersistenceException, IllegalArgumentException, IOException, try-catch-finally
- ✅ **Geração de IDs**: GenerationType.IDENTITY com PostgreSQL
- ✅ **Encapsulamento**: Atributos privados, métodos com responsabilidade única
- ✅ **Nomenclatura Clara**: Nomes descritivos em português/inglês
- ✅ **JavaFX Configurado**: Environment correto, module-info.java atualizado
- ✅ **Paths de Arquivos**: Estrutura pronta para usar Paths.get()
- ✅ **Documentação**: Javadoc em todas as classes e métodos

---

**Versão**: 1.0  
**Autor**: Adrian  
**Data**: Dezembro 2025  
