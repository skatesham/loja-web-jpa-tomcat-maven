package br.com.caelum.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.caelum.model.Loja;
import br.com.caelum.model.Produto;

@Repository
public class ProdutoDao {

	@PersistenceContext
	private EntityManager em;

	public List<Produto> getProdutos() {
		
		/* TESTE POOL
		try {
			ComboPooledDataSource dataSource = (ComboPooledDataSource) new JpaConfigurator().getDataSource();

			for (int i = 0; i < 10; i++) {
				dataSource.getConnection();

				System.out.println(i + " - Conexões existentes: " + dataSource.getNumConnections());
				System.out.println(i + " - Conexões ocupadas: " + dataSource.getNumBusyConnections());
				System.out.println(i + " - Conexões ociosas: " + dataSource.getNumIdleConnections());

				System.out.println("");
				
			}
		} catch (Exception e) {

		}
		*/
		
		
		return em.createQuery("select distinct p from Produto p", Produto.class)
				.setHint("org.hibernate.cacheable", "true")
				.setHint("javax.persistence.loadgraph", em.getEntityGraph("produtoComCategoria"))
				.getResultList();
	
	}

	// EAGER GET
	public List<Produto> getProdutosComCategorias() {
		return em.createQuery("select distinct p from Produto p join fetch p.categorias", Produto.class)
				.getResultList();
	}

	public Produto getProduto(Integer id) {
		Produto produto = em.find(Produto.class, id);
		return produto;
	}

	// Criteria Simplificada https://github.com/uaihebert/uaicriteria
	public List<Produto> getProdutos(String nome, Integer categoriaId, Integer lojaId) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Produto> query = criteriaBuilder.createQuery(Produto.class);
		Root<Produto> root = query.from(Produto.class);

		Path<String> nomePath = root.<String>get("nome");
		Path<Integer> lojaPath = root.<Loja>get("loja").<Integer>get("id");
		Path<Integer> categoriaPath = root.join("categorias").<Integer>get("id");

		List<Predicate> predicates = new ArrayList<>();

		if (!nome.isEmpty()) {
			Predicate nomeIgual = criteriaBuilder.like(nomePath, nome);
			predicates.add(nomeIgual);
		}
		if (categoriaId != null) {
			Predicate categoriaIgual = criteriaBuilder.equal(categoriaPath, categoriaId);
			predicates.add(categoriaIgual);
		}
		if (lojaId != null) {
			Predicate lojaIgual = criteriaBuilder.equal(lojaPath, lojaId);
			predicates.add(lojaIgual);
		}

		query.where((Predicate[]) predicates.toArray(new Predicate[0]));

		TypedQuery<Produto> typedQuery = em.createQuery(query);
		return typedQuery.getResultList();

	}

	public void insere(Produto produto) {
		if (produto.getId() == null)
			em.persist(produto);
		else
			em.merge(produto);
	}

}
