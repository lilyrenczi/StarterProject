package org.czi.kb.service_example.example;

import org.czi.rato.api.cluster.service.MetaServiceContext;
import org.czi.rato.api.data.DataRepository;
import org.czi.rato.api.data.Session;
import org.czi.rato.api.data.SessionFactory;
import org.czi.rato.api.data.Transaction;
import org.czi.rato.api.model.datagrid.Citation;
import org.czi.rato.api.model.datagrid.Publication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Example Supporting Class for Service.
 * Created by Dave Ellery on 27/02/17.
 */
@Component
class ExampleComponent {
    private static final Logger logger = LoggerFactory.getLogger(ExampleComponent.class);
    private static final String description = "Example Component to link citations";
    private final DataRepository<Long, Publication> publicationDataRepository;
//    private final DataRepository<Long, Citation> citationDataRepository;
    private final SessionFactory sessionFactory;

    @Autowired
    ExampleComponent(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.publicationDataRepository = sessionFactory.getOrCreateSession().getRepository(Publication.class);
//        this.citationDataRepository = sessionFactory.getOrCreateSession().getRepository(Citation.class);
    }

    String getInfo() {
        return description;
    }

    void linkCitation(final MetaServiceContext context) {

        // example of starting transaction, notice it is committed at the end.
        final Session session = sessionFactory.getOrCreateSession();
        final Transaction tx = session.startTransaction();

        // example of rato cache object creation, both are valid
        final Publication publication = session.newInstance(Publication.class);
        final Citation citation = citationDataRepository.newInstance();

        // example of object modification, only take effect in knowledge graph after commit
        publication.getLanguages().add("en");
        citation.setFromPaper(publication);

        // example of getting object in question from ticket
        final Publication toPaper =  context.getData();
        citation.setToPaper(toPaper);

        // example of adding content to transaction
        session.save(publication);
        citationDataRepository.save(citation);

        // transactions should last short (< 1 sec) amount of time for optimized performance
        // tune the frequency of transaction so that each transaction last 0.1 - 1 sec
        tx.commit();

        // example of data repository usage
        final Publication savedPublication = publicationDataRepository.query()
                .equals("Languages", "en").getOne();

        // example of exception throwing
        if (savedPublication == null) {
            throw new NullPointerException("Object Not Found");
        }

        // example of logging information to track progress or stack, used in replace of System.out.print
        logger.info("Paper with id {} Saved Successfully", savedPublication.getId());
    }
}
