package org.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
public class ContatoDAO {

    private static final Logger LOGGER = Logger.getLogger(ContatoDAO.class.getName());
    private static final String PERSISTENCE_UNIT = "un-jpa";

    /**
     * Salva um novo contato no banco de dados.
     *
     * @param contato o contato a ser salvo
     * @throws IllegalArgumentException se o contato for nulo
     */
    public static void salvarContato(Contato contato) {
        if (contato == null) {
            throw new IllegalArgumentException("Contato não pode ser nulo");
        }

        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
            em = emf.createEntityManager();
            em.getTransaction().begin();
            em.persist(contato);
            em.getTransaction().commit();
            LOGGER.info("Contato salvo com sucesso: " + contato.getNome());
        } catch (PersistenceException e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            LOGGER.log(Level.SEVERE, "Erro ao salvar contato: " + contato.getNome(), e);
            throw new RuntimeException("Erro ao salvar contato no banco de dados", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao salvar contato", e);
            throw new RuntimeException("Erro inesperado ao salvar contato", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }

    /**
     * Lista todos os contatos cadastrados no banco de dados.
     *
     * @return lista de todos os contatos ou lista vazia se houver erro
     */
    public static List<Contato> listarContatos() {
        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
            em = emf.createEntityManager();
            TypedQuery<Contato> query = em.createQuery("SELECT c FROM Contato c ORDER BY c.nome", Contato.class);
            List<Contato> contatos = query.getResultList();
            LOGGER.info("Total de contatos listados: " + contatos.size());
            return contatos;
        } catch (PersistenceException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar contatos", e);
            throw new RuntimeException("Erro ao listar contatos do banco de dados", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao listar contatos", e);
            throw new RuntimeException("Erro inesperado ao listar contatos", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }

    /**
     * Atualiza um contato existente no banco de dados.
     *
     * @param contato o contato com dados atualizados
     * @throws IllegalArgumentException se o contato for nulo ou não tiver ID
     */
    public static void atualizarContato(Contato contato) {
        if (contato == null) {
            throw new IllegalArgumentException("Contato não pode ser nulo");
        }
        if (contato.getId() <= 0) {
            throw new IllegalArgumentException("Contato deve ter um ID válido para atualização");
        }

        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
            em = emf.createEntityManager();
            em.getTransaction().begin();
            em.merge(contato);
            em.getTransaction().commit();
            LOGGER.info("Contato atualizado com sucesso: " + contato.getNome());
        } catch (PersistenceException e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            LOGGER.log(Level.SEVERE, "Erro ao atualizar contato: " + contato.getNome(), e);
            throw new RuntimeException("Erro ao atualizar contato no banco de dados", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao atualizar contato", e);
            throw new RuntimeException("Erro inesperado ao atualizar contato", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }

    /**
     * Exclui um contato do banco de dados pelo seu ID.
     *
     * @param id o ID do contato a ser excluído
     * @throws IllegalArgumentException se o ID for inválido
     */
    public static void excluirContato(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID do contato deve ser maior que zero");
        }

        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
            em = emf.createEntityManager();
            em.getTransaction().begin();

            Contato contato = em.find(Contato.class, id);
            if (contato != null) {
                em.remove(contato);
                em.getTransaction().commit();
                LOGGER.info("Contato excluído com sucesso. ID: " + id);
            } else {
                em.getTransaction().rollback();
                LOGGER.warning("Contato não encontrado para exclusão. ID: " + id);
                throw new RuntimeException("Contato com ID " + id + " não encontrado");
            }
        } catch (PersistenceException e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            LOGGER.log(Level.SEVERE, "Erro ao excluir contato com ID: " + id, e);
            throw new RuntimeException("Erro ao excluir contato do banco de dados", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao excluir contato", e);
            throw new RuntimeException("Erro inesperado ao excluir contato", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }

    /**
     * Busca um contato pelo seu ID.
     *
     * @param id o ID do contato
     * @return o contato encontrado ou null se não existir
     */
    public static Contato buscarPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID do contato deve ser maior que zero");
        }

        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
            em = emf.createEntityManager();
            return em.find(Contato.class, id);
        } catch (PersistenceException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar contato com ID: " + id, e);
            throw new RuntimeException("Erro ao buscar contato no banco de dados", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao buscar contato", e);
            throw new RuntimeException("Erro inesperado ao buscar contato", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }
}
