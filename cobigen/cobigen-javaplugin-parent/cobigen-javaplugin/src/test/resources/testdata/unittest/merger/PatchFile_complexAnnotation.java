import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;

@NamedEntityGraphs(
    value = { @NamedEntityGraph(name = "ttt",
        attributeNodes = { //
            @NamedAttributeNode(value = "ttt", subgraph = "ttt")}, //
        subgraphs = {
            @NamedSubgraph(name = "ttt",
                attributeNodes = @NamedAttributeNode("zzz")), //
            @NamedSubgraph(name = "zzz", attributeNodes = @NamedAttributeNode("aaa")) }) //
    })
public class Whatever {}