package io.github.wodt.semantics;

import io.github.webbasedwodt.model.ontology.DigitalTwinSemantics;
import io.github.webbasedwodt.model.ontology.rdf.*;
import io.github.wodt.model.Position;
import it.wldt.core.state.DigitalTwinStateAction;
import it.wldt.core.state.DigitalTwinStateProperty;
import it.wldt.core.state.DigitalTwinStateRelationship;
import it.wldt.core.state.DigitalTwinStateRelationshipInstance;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CarSemantics implements DigitalTwinSemantics {

    private static final Map<String, RdfUriResource> PROPERTIES_DOMAIN_TAG = Map.of(
            "position", new RdfUriResource(URI.create("http://www.example.org/ontology#CarPosition"))
    );

    @Override
    public List<RdfClass> getDigitalTwinTypes() {
        return List.of(new RdfClass(URI.create("http://www.example.org/ontology#Car")), new RdfClass(URI.create("http://www.example.org/ontology#Vehicle")));
    }

    @Override
    public Optional<RdfUriResource> getDomainTag(DigitalTwinStateProperty<?> property) {
        return getOptionalFromMap(PROPERTIES_DOMAIN_TAG, property.getKey());
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
        if ("position".equals(property.getKey())) {
            return Optional.of(List.of(
                    new RdfUnSubjectedTriple(
                            new RdfProperty(URI.create("http://www.example.org/ontology#CarPosition")),
                            new RdfBlankNode("CarPosition", List.of(
                                    new RdfUnSubjectedTriple(
                                            new RdfProperty(URI.create("http://www.example.org/ontology#CarLatitude")),
                                            new RdfBlankNode("CarLatitude", List.of(
                                                    new RdfUnSubjectedTriple(
                                                            new RdfProperty(URI.create("http://www.example.org/ontology#v")),
                                                            new RdfLiteral<>(((Position) property.getValue()).getLatitude())
                                                    )
                                            ))
                                    ),
                                    new RdfUnSubjectedTriple(
                                            new RdfProperty(URI.create("http://www.example.org/ontology#CarLongitude")),
                                            new RdfBlankNode("CarLongitude", List.of(
                                                    new RdfUnSubjectedTriple(
                                                            new RdfProperty(URI.create("http://www.example.org/ontology#v")),
                                                            new RdfLiteral<>(((Position) property.getValue()).getLongitude())
                                                    )
                                            ))
                                    )

                            ))

                    )
            ));
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<RdfUnSubjectedTriple>> mapData(DigitalTwinStateRelationshipInstance<?> relationshipInstance) {
        return Optional.empty();
    }

    private <T> Optional<T> getOptionalFromMap(final Map<String, T> map, final String key) {
        if (map.containsKey(key)) {
            return Optional.of(map.get(key));
        }
        return Optional.empty();
    }
}
