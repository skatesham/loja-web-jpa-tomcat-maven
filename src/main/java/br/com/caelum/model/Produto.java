
package br.com.caelum.model;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
//import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;


// SOLUÇÃO PROBLEMA DE N+1 COM SOLUCÃO EM PRODUTODAO
@NamedEntityGraphs({
    @NamedEntityGraph(name = "produtoComCategoria", 
                      attributeNodes = { 
                            @NamedAttributeNode("categorias") 
                      }) 
})

@Entity
public @Data class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@NotEmpty
	private String nome;
	@NotEmpty
	private String linkDaFoto;

	@NotEmpty
	@Column(columnDefinition = "TEXT")
	private String descricao;

	@Min(20)
	private double preco;

	@Valid
	@ManyToOne
	private Loja loja;
	
	//@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name="Produto_Categoria", 
			joinColumns = @JoinColumn(name="produto_id", referencedColumnName="id"),
			inverseJoinColumns = @JoinColumn(name="categoria_id", referencedColumnName = "id"))
	@ManyToMany
	private List<Categoria> categorias = new LinkedList<>();

	// método auxiliar para associar categorias com o produto
	// se funcionar apos ter definido o relacionamento entre produto e categoria
	public void adicionarCategorias(Categoria... categorias) {
		for (Categoria categoria : categorias) {
			this.categorias.add(categoria);
		}
	}

}
