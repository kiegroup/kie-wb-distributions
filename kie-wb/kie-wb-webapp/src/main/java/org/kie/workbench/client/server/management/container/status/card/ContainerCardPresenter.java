package org.kie.workbench.client.server.management.container.status.card;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.workbench.client.server.management.events.RemoteServerSelected;
import org.kie.workbench.client.server.management.widget.card.CardPresenter;
import org.kie.workbench.client.server.management.widget.card.body.BodyPresenter;
import org.kie.workbench.client.server.management.widget.card.footer.FooterPresenter;
import org.kie.workbench.client.server.management.widget.card.title.LinkTitlePresenter;
import org.uberfire.mvp.Command;

@Dependent
public class ContainerCardPresenter {

    public interface View extends IsWidget {

        void setCard( CardPresenter.View card );
    }

    private final View view;

    private final Event<RemoteServerSelected> remoteServerSelectedEvent;

    @Inject
    public ContainerCardPresenter( final View view,
                                   final Event<RemoteServerSelected> remoteServerSelectedEvent ) {
        this.view = view;
        this.remoteServerSelectedEvent = remoteServerSelectedEvent;
    }

    public View getView() {
        return view;
    }

    public void setup( final ServerInstanceKey serverInstanceKey,
                       final Container container ) {
        final LinkTitlePresenter linkTitlePresenter = newTitle();
        linkTitlePresenter.setup( serverInstanceKey,
                                  new Command() {
                                      @Override
                                      public void execute() {
                                          remoteServerSelectedEvent.fire( new RemoteServerSelected( serverInstanceKey ) );
                                      }
                                  } );
        final BodyPresenter bodyPresenter = newBody();
        bodyPresenter.setMessages( container.getMessages() );

        final FooterPresenter footerPresenter = newFooter();
        footerPresenter.setup( container.getUrl(), container.getResolvedReleasedId().getVersion() );

        CardPresenter card = newCard();
        card.addTitle( linkTitlePresenter );
        card.addBody( bodyPresenter );
        card.addFooter( footerPresenter );

        this.view.setCard( card.getView() );
    }

    CardPresenter newCard() {
        return IOC.getBeanManager().lookupBean( CardPresenter.class ).getInstance();
    }

    LinkTitlePresenter newTitle() {
        return IOC.getBeanManager().lookupBean( LinkTitlePresenter.class ).getInstance();
    }

    BodyPresenter newBody() {
        return IOC.getBeanManager().lookupBean( BodyPresenter.class ).getInstance();
    }

    FooterPresenter newFooter() {
        return IOC.getBeanManager().lookupBean( FooterPresenter.class ).getInstance();
    }

}
