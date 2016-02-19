package org.kie.workbench.client.server.management.widget.card.title;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.model.GAV;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class InfoTitlePresenter implements TitlePresenter {

    public interface View extends IsWidget {

        void setup( final String groupId,
                    final String artifactId );
    }

    private final View view;

    @Inject
    public InfoTitlePresenter( final View view ) {
        this.view = view;
    }

    @Override
    public View getView() {
        return view;
    }

    public void setup( final GAV gav ) {
        checkNotNull( "gav", gav );
        view.setup( gav.getGroupId(), gav.getArtifactId() );
    }

}
