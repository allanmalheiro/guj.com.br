package br.com.caelum.guj.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import net.jforum.entities.UserSession;

import org.junit.Test;

import br.com.caelum.guj.dao.GUJUserDAO;
import br.com.caelum.guj.model.NewsletterParticipant;
import br.com.caelum.guj.newsletter.NewsletterSubscriber;
import br.com.caelum.vraptor.util.test.MockResult;

public class NewsletterControllerTest {

	@Test
	public void should_register_the_given_user_if_its_not_a_participant_yet() throws URISyntaxException {
		Integer gujUserId = 1;
		NewsletterSubscriber manager = mock(NewsletterSubscriber.class);
		GUJUserDAO dao = mock(GUJUserDAO.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		UserSession userSession = mock(UserSession.class);

		when(dao.findParticipantByGujUserId(gujUserId)).thenReturn(null);
		when(dao.emailFromUser(gujUserId)).thenReturn("email@email.com");
		when(request.getAttribute("userSession")).thenReturn(userSession);

		NewsletterController controller = new NewsletterController(dao, request, new MockResult(), manager);
		controller.register(gujUserId);

		verify(dao).registerNewsletterParticipant(any(NewsletterParticipant.class));
		verify(userSession).setNewsletterParticipant(true);
		verify(manager).subscribe(any(NewsletterParticipant.class));
	}

	@Test
	public void should_not_register_the_given_user_if_its_already_a_participant() throws URISyntaxException {
		Integer gujUserId = 1;
		NewsletterSubscriber manager = mock(NewsletterSubscriber.class);

		GUJUserDAO dao = mock(GUJUserDAO.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		UserSession userSession = mock(UserSession.class);

		when(dao.findParticipantByGujUserId(gujUserId)).thenReturn(new NewsletterParticipant());
		when(request.getAttribute("userSession")).thenReturn(userSession);

		NewsletterController controller = new NewsletterController(dao, request, new MockResult(), manager);
		controller.register(gujUserId);

		verify(dao, never()).registerNewsletterParticipant(any(NewsletterParticipant.class));
		verify(userSession, never()).setNewsletterParticipant(true);
		verify(manager, never()).subscribe(any(NewsletterParticipant.class));
	}
}