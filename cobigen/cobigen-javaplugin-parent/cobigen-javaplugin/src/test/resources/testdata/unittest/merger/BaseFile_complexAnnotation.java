import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;

@NamedEntityGraphs(
    value = { @NamedEntityGraph(name = "xxx",
        attributeNodes = { //
            @NamedAttributeNode(value = "xxx", subgraph = "xxx")}, //
        subgraphs = {
            @NamedSubgraph(name = "xxx",
                attributeNodes = @NamedAttributeNode("zzz")), //
            @NamedSubgraph(name = "zzz", attributeNodes = @NamedAttributeNode("aaa")) }) //
    })
public class Whatever {}