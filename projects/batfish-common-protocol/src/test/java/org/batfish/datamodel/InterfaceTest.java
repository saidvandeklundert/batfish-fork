package org.batfish.datamodel;

import static org.batfish.datamodel.Interface.computeCiscoInterfaceType;
import static org.batfish.datamodel.Interface.isRealInterfaceName;
import static org.batfish.datamodel.matchers.InterfaceMatchers.isActive;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.common.testing.EqualsTester;
import org.batfish.common.util.BatfishObjectMapper;
import org.batfish.datamodel.Interface.Dependency;
import org.batfish.datamodel.Interface.DependencyType;
import org.batfish.datamodel.ospf.OspfInterfaceSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests of {@link org.batfish.datamodel.Interface} */
@RunWith(JUnit4.class)
public class InterfaceTest {

  @Test
  public void testDependencyEquals() {
    new EqualsTester()
        .addEqualityGroup(
            new Dependency("i1", DependencyType.BIND), new Dependency("i1", DependencyType.BIND))
        .addEqualityGroup(new Dependency("i1", DependencyType.AGGREGATE))
        .addEqualityGroup(new Dependency("i2", DependencyType.BIND))
        .testEquals();
  }

  @Test
  public void testBlacklisting() {
    Interface i = Interface.builder().setName("iface").build();
    assertFalse("Interface is not blacklisted", i.getBlacklisted());
    assertThat(i, isActive());

    i.blacklist();

    assertTrue("Interface is blacklisted", i.getBlacklisted());
    assertThat(i, not(isActive()));
  }

  @Test
  public void testInterfaceType() {
    assertThat(computeCiscoInterfaceType("TenGigE0/5/0/5/8"), equalTo(InterfaceType.PHYSICAL));
    assertThat(computeCiscoInterfaceType("TenGigE0/5/0/5/8.1"), equalTo(InterfaceType.LOGICAL));
  }

  @Test
  public void testRealInterfaceName() {
    assertThat(isRealInterfaceName("Ethernet0"), equalTo(true));
    assertThat(isRealInterfaceName("ge-0/0/0"), equalTo(true));
    assertThat(isRealInterfaceName("asdfasdf"), equalTo(true));
    assertThat(isRealInterfaceName("null_interface"), equalTo(false));
    assertThat(isRealInterfaceName("unset_local_interface"), equalTo(false));
    assertThat(isRealInterfaceName("invalid_local_interface"), equalTo(false));
    assertThat(isRealInterfaceName("dynamic"), equalTo(false));
  }

  @Test
  public void testRoutingPolicySettingInBuilder() {
    String policy = "some_policy";
    Interface i = Interface.builder().setName("iface").setPacketPolicy(policy).build();
    assertThat(i.getPacketPolicyName(), equalTo(policy));
  }

  @Test
  public void testSerialization() {
    // TODO: more thorough testing
    Interface i =
        Interface.builder()
            .setMtu(7)
            .setName("ifaceName")
            .setOspfSettings(OspfInterfaceSettings.defaultSettingsBuilder().build())
            .setHmm(true)
            .build();

    // test (de)serialization
    Interface iDeserial = BatfishObjectMapper.clone(i, Interface.class);
    assertThat(i, equalTo(iDeserial));
  }

  @Test
  public void testJacksonSerialization() {
    // TODO: more thorough testing
    Interface obj = Interface.builder().setName("ifaceName").setHmm(true).build();
    assertEquals(obj, BatfishObjectMapper.clone(obj, Interface.class));
  }

  @Test
  public void testEquals() {
    // TODO: more thorough testing
    Interface.Builder b = Interface.builder().setName("ifaceName");
    new EqualsTester()
        .addEqualityGroup(b.build(), b.build())
        .addEqualityGroup(b.setHmm(true).build())
        .addEqualityGroup(b.setName("iface2").build())
        .testEquals();
  }
}
