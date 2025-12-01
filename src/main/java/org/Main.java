package org;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.model.Contato;
import org.model.ContatoDAO;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        // Criar e salvar contatos
        Contato contato1 = new Contato();
        contato1.setNome("João Silva");
        contato1.setTelefone("(11) 99999-9999");
        contato1.setEmail("joao@email.com");
        ContatoDAO.salvarContato(contato1);

        Contato contato2 = new Contato();
        contato2.setNome("Maria Santos");
        contato2.setTelefone("(11) 88888-8888");
        contato2.setEmail("maria@email.com");
        ContatoDAO.salvarContato(contato2);

        Contato contato3 = new Contato();
        contato3.setNome("Pedro Costa");
        contato3.setTelefone("(11) 77777-7777");
        contato3.setEmail("pedro@email.com");
        ContatoDAO.salvarContato(contato3);

        System.out.println("Contatos salvos!");

        // Listar todos os contatos
        List<Contato> contatos = ContatoDAO.listarContatos();
        System.out.println("\nTotal de contatos: " + contatos.size());

        for (Contato c : contatos) {
            System.out.println("ID: " + c.getId() + ", Nome: " + c.getNome() +
                    ", Tel: " + c.getTelefone() + ", Email: " + c.getEmail());
        }
    }
}