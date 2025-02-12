package org.batfish.grammar.flatjuniper;

import static org.batfish.grammar.flatjuniper.ConfigurationBuilder.unquote;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.batfish.grammar.flatjuniper.FlatJuniperParser.Flat_juniper_configurationContext;
import org.batfish.grammar.flatjuniper.FlatJuniperParser.S_groups_namedContext;
import org.batfish.grammar.flatjuniper.FlatJuniperParser.S_groups_tailContext;
import org.batfish.grammar.flatjuniper.FlatJuniperParser.Set_lineContext;
import org.batfish.grammar.flatjuniper.FlatJuniperParser.Set_line_tailContext;
import org.batfish.grammar.flatjuniper.FlatJuniperParser.StatementContext;
import org.batfish.grammar.flatjuniper.Hierarchy.HierarchyTree;
import org.batfish.grammar.flatjuniper.Hierarchy.HierarchyTree.HierarchyPath;

public class GroupTreeBuilder extends FlatJuniperParserBaseListener {

  private Flat_juniper_configurationContext _configurationContext;

  private HierarchyPath _currentPath;

  private Set_lineContext _currentSetLine;

  private boolean _enablePathRecording;

  /** Whether wildcards are valid in the rest of the current line, or normal text otherwise. */
  private boolean _enableWildcards;

  private final Hierarchy _hierarchy;

  private List<ParseTree> _newConfigurationLines;

  public GroupTreeBuilder(Hierarchy hierarchy) {
    _hierarchy = hierarchy;
  }

  @Override
  public void enterFlat_juniper_configuration(Flat_juniper_configurationContext ctx) {
    _configurationContext = ctx;
    _newConfigurationLines = new ArrayList<>(ctx.children);
  }

  @Override
  public void enterSet_line(Set_lineContext ctx) {
    _currentSetLine = ctx;
  }

  @Override
  public void enterS_groups_tail(S_groups_tailContext ctx) {
    _enablePathRecording = true;
  }

  @Override
  public void exitS_groups_tail(S_groups_tailContext ctx) {
    _enablePathRecording = false;
  }

  @Override
  public void exitFlat_juniper_configuration(Flat_juniper_configurationContext ctx) {
    _configurationContext.children = _newConfigurationLines;
  }

  @Override
  public void enterS_groups_named(S_groups_namedContext ctx) {
    _currentPath = new HierarchyPath();
    _enableWildcards = true;
  }

  @Override
  public void exitS_groups_named(S_groups_namedContext ctx) {
    _enableWildcards = false;
    HierarchyPath path = _currentPath;
    assert path != null;
    _currentPath = null;
    String groupName = unquote(ctx.name.getText()).orElse(ctx.name.getText());
    HierarchyTree tree = _hierarchy.getTree(groupName);
    if (tree == null) {
      tree = _hierarchy.newTree(groupName);
    }
    StatementContext statement = ctx.s_groups_tail().statement();
    if (statement == null) {
      return;
    }
    path.setStatement(statement);
    tree.addPath(path, _currentSetLine, null, null);
  }

  @Override
  public void exitSet_line(Set_lineContext ctx) {
    _currentSetLine = null;
  }

  @Override
  public void exitSet_line_tail(Set_line_tailContext ctx) {
    _enablePathRecording = false;
  }

  @Override
  public void visitTerminal(TerminalNode node) {
    if (_enablePathRecording) {
      String text = node.getText();
      int line = node.getSymbol().getLine();
      if (_enableWildcards && node.getSymbol().getType() == FlatJuniperLexer.WILDCARD) {
        _currentPath.addWildcardNode(text, line);
      } else {
        _currentPath.addNode(text, line);
      }
    }
  }
}
