package io.github.wodt.semantics;

import io.github.webbasedwodt.model.ontology.DigitalTwinSemantics;
import io.github.webbasedwodt.model.ontology.rdf.*;
import it.wldt.core.state.DigitalTwinStateAction;
import it.wldt.core.state.DigitalTwinStateProperty;
import it.wldt.core.state.DigitalTwinStateRelationship;
import it.wldt.core.state.DigitalTwinStateRelationshipInstance;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PlateReaderSemantics implements DigitalTwinSemantics {

    private static final Map<String, RdfUriResource> RELATIONSHIP_DOMAIN_TAG = Map.of(
            "vehicle-detection", new RdfUriResource(URI.create("http://www.example.org/ontology#Car-Detection"))
    );

    @Override
    public List<RdfClass> getDigitalTwinTypes() {
        return List.of(new RdfClass(URI.create("http://www.example.org/ontology#PlateReader")));
    }

    @Override
    public Optional<RdfUriResource> getDomainTag(DigitalTwinStateProperty<?> property) {
        return Optional.empty();
    }

    @Override
    public Optional<RdfUriResource> getDomainTag(DigitalTwinStateRelationship<?> relationship) {
        return getOptionalFromMap(RELATIONSHIP_DOMAIN_TAG, relationship.getName());
    }

    @Override
    public Optional<RdfUriResource> getDomainTag(DigitalTwinStateAction action) {
        return Optional.empty();
    }

    @Override
    public Optional<List<RdfUnSubjectedTriple>> mapData(DigitalTwinStateProperty<?> property) {
        return Optional.empty();
    }

    @Override
    public Optional<List<RdfUnSubjectedTriple>> mapData(DigitalTwinStateRelationshipInstance<?> relationshipInstance) {

        return getOptionalFromMap(RELATIONSHIP_DOMAIN_TAG, relationshipInstance.getRelationshipName()).map(uri ->
                List.of(
                        new RdfUnSubjectedTriple(
                                new RdfProperty(uri.getUri().get()),
                                new RdfIndividual(URI.create(relationshipInstance.getTargetId().toString()))
                        )
                ));
    }

    private <T> Optional<T> getOptionalFromMap(final Map<String, T> map, final String key) {
        if (map.containsKey(key)) {
            return Optional.of(map.get(key));
        }
        return Optional.empty();
    }
}
