package io.github.wodt.semantics;

import io.github.webbasedwodt.model.ontology.DigitalTwinSemantics;
import io.github.webbasedwodt.model.ontology.rdf.RdfClass;
import io.github.webbasedwodt.model.ontology.rdf.RdfUnSubjectedTriple;
import io.github.webbasedwodt.model.ontology.rdf.RdfUriResource;
import it.wldt.core.state.DigitalTwinStateAction;
import it.wldt.core.state.DigitalTwinStateProperty;
import it.wldt.core.state.DigitalTwinStateRelationship;
import it.wldt.core.state.DigitalTwinStateRelationshipInstance;

import java.util.List;
import java.util.Optional;

public class PlateReaderSemantics implements DigitalTwinSemantics {
    @Override
    public List<RdfClass> getDigitalTwinTypes() {
        return null;
    }

    @Override
    public Optional<RdfUriResource> getDomainTag(DigitalTwinStateProperty<?> property) {
        return Optional.empty();
    }

    @Override
    public Optional<RdfUriResource> getDomainTag(DigitalTwinStateRelationship<?> relationship) {
        return Optional.empty();
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
        return Optional.empty();
    }
}
