package org.batfish.datamodel.ospf;

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.batfish.datamodel.ConcreteInterfaceAddress;
import org.batfish.datamodel.collections.NodeInterfacePair;

/** Uniquely identifies an OSPF configuration ({@link OspfNeighbor}) in the network. */
@ParametersAreNonnullByDefault
public final class OspfNeighborConfigId implements Serializable {

  private static final String PROP_HOSTNAME = "hostname";
  private static final String PROP_VRF = "vrf";
  private static final String PROP_PROCESS = "process";
  private static final String PROP_INTERFACE = "interface";
  private static final String PROP_ADDRESS = "address";

  private final @Nonnull String _hostname;
  private final @Nonnull String _vrfName;
  private final @Nonnull String _procName;
  private final @Nonnull String _interfaceName;
  private final @Nonnull ConcreteInterfaceAddress _address;

  /**
   * Create a new unique identifier for an OSPF process
   *
   * @param hostname the hostname on which the neighbor exists
   * @param vrfName the name of a VRF on which the neighbor exists
   * @param procName the name of OSPF process on which the neighbor exists
   * @param interfaceName the interface name
   * @param address the interface address
   */
  public OspfNeighborConfigId(
      String hostname,
      String vrfName,
      String procName,
      String interfaceName,
      ConcreteInterfaceAddress address) {
    _hostname = hostname;
    _vrfName = vrfName;
    _procName = procName;
    _interfaceName = interfaceName;
    _address = address;
  }

  @JsonCreator
  private static OspfNeighborConfigId create(
      @Nullable @JsonProperty(PROP_HOSTNAME) String hostname,
      @Nullable @JsonProperty(PROP_VRF) String vrf,
      @Nullable @JsonProperty(PROP_PROCESS) String process,
      @Nullable @JsonProperty(PROP_INTERFACE) String interfaceName,
      @Nullable @JsonProperty(PROP_ADDRESS) ConcreteInterfaceAddress address) {
    checkArgument(hostname != null, "Missing %s", PROP_HOSTNAME);
    checkArgument(vrf != null, "Missing %s", PROP_VRF);
    checkArgument(process != null, "Missing %s", PROP_PROCESS);
    checkArgument(interfaceName != null, "Missing %s", PROP_INTERFACE);
    checkArgument(address != null, "Missing %s", PROP_ADDRESS);
    return new OspfNeighborConfigId(hostname, vrf, process, interfaceName, address);
  }

  @Nonnull
  @JsonProperty(PROP_HOSTNAME)
  public String getHostname() {
    return _hostname;
  }

  @Nonnull
  @JsonProperty(PROP_VRF)
  public String getVrfName() {
    return _vrfName;
  }

  @Nonnull
  @JsonProperty(PROP_PROCESS)
  public String getProcName() {
    return _procName;
  }

  @Nonnull
  @JsonProperty(PROP_INTERFACE)
  public String getInterfaceName() {
    return _interfaceName;
  }

  @Nonnull
  @JsonProperty(PROP_ADDRESS)
  public ConcreteInterfaceAddress getAddress() {
    return _address;
  }

  @Nonnull
  @JsonIgnore
  public NodeInterfacePair getNodeInterfacePair() {
    return NodeInterfacePair.of(getHostname(), getInterfaceName());
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof OspfNeighborConfigId)) {
      return false;
    }
    OspfNeighborConfigId other = (OspfNeighborConfigId) o;
    return _hostname.equals(other._hostname)
        && _vrfName.equals(other._vrfName)
        && _procName.equals(other._procName)
        && _interfaceName.equals(other._interfaceName)
        && _address.equals(other._address);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_hostname, _vrfName, _procName, _interfaceName, _address);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("hostname", _hostname)
        .add("vrfName", _vrfName)
        .add("procName", _procName)
        .add("interfaceName", _interfaceName)
        .add(PROP_ADDRESS, _address)
        .toString();
  }
}
