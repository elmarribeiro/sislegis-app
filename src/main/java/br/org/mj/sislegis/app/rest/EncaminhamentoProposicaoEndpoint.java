package br.org.mj.sislegis.app.rest;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import br.org.mj.sislegis.app.model.EncaminhamentoProposicao;
import br.org.mj.sislegis.app.model.Usuario;
import br.org.mj.sislegis.app.service.EncaminhamentoProposicaoService;
import br.org.mj.sislegis.app.service.TarefaService;
import br.org.mj.sislegis.app.service.UsuarioService;

@Path("/encaminhamentoProposicao")
public class EncaminhamentoProposicaoEndpoint {

	@Inject
	private EncaminhamentoProposicaoService service;

	@Inject
	private UsuarioService usuarioService;
	
//	@Inject
	private TarefaService tarefaService;

	@POST
	@Consumes("application/json")
	public Response create(EncaminhamentoProposicao entity) {
		EncaminhamentoProposicao savedEntity = service.salvarEncaminhamentoProposicao(entity);
		
		return Response.created(
				UriBuilder.fromResource(EncaminhamentoEndpoint.class)
						.path(String.valueOf(savedEntity.getId())).build()).build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	public Response deleteById(@PathParam("id") Long id) {
		service.deleteById(id);
		return Response.noContent().build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public Response findById(@PathParam("id") Long id) {
		return Response.ok(service.findById(id)).build();
	}

	@GET
	@Produces("application/json")
	public List<EncaminhamentoProposicao> listAll(
			@QueryParam("start") Integer startPosition,
			@QueryParam("max") Integer maxResult) {
		return service.listAll();
	}

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response update(EncaminhamentoProposicao entity) {
		try {
			entity = service.salvarEncaminhamentoProposicao(entity);
		} catch (OptimisticLockException e) {
			return Response.status(Response.Status.CONFLICT)
					.entity(e.getEntity()).build();
		}

		return Response.noContent().build();
	}

	@GET
	@Path("/proposicao/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public List<EncaminhamentoProposicao> findByProposicao(@PathParam("id") Long id) {
		final List<EncaminhamentoProposicao> results = service.findByProposicao(id);
		for (Iterator<EncaminhamentoProposicao> iterator = results.iterator(); iterator.hasNext();) {
			EncaminhamentoProposicao encaminhamentoProposicao = iterator.next();
			Usuario u = usuarioService.findById(encaminhamentoProposicao.getResponsavel().getId());
			encaminhamentoProposicao.setResponsavel(u);
			encaminhamentoProposicao.getComentario().setAutor(u);
		}
		return results;
	}

}
