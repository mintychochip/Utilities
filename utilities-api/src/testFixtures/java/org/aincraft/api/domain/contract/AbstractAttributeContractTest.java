package org.aincraft.api.domain.contract;

import static org.junit.jupiter.api.Assertions.*;

import net.kyori.adventure.key.Key;
import org.aincraft.api.domain.attribute.Attribute;
import org.aincraft.api.domain.attribute.AttributeInstance;
import org.aincraft.api.domain.attribute.AttributeModifier;
import org.aincraft.api.domain.datacomponent.item.EquipmentSlotGroup;
import org.aincraft.api.domain.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

/**
 * Contract tests for {@link Attribute} and {@link AttributeModifier}. Verifies cross-platform
 * invariants: modifier key uniqueness, amount/op-name shape, and slotGroup round-trip.
 */
public abstract class AbstractAttributeContractTest {

  protected abstract Attribute createAttributeFixture(Key key, double defaultValue);

  protected abstract AttributeInstance createInstanceFixture(Attribute attribute, double baseValue);

  protected abstract AttributeModifier createModifierFixture(
      Key key, double amount, AttributeModifier.Operation operation, EquipmentSlot slot);

  protected abstract AttributeModifier createSlotGroupModifierFixture(
      Key key, double amount, AttributeModifier.Operation operation, EquipmentSlotGroup slotGroup);

  @Test
  void testAttributeKeyAndDefaultRoundTrip() {
    Key key = Key.key("aincraft", "test_strength");
    Attribute attr = createAttributeFixture(key, 7.0);
    assertEquals(key, attr.key());
    assertEquals(
        7.0,
        attr.getDefaultValue(),
        0.0,
        "Attribute default value must round-trip through the adapter");
  }

  @Test
  void testAttributeSentimentAlwaysPresent() {
    Attribute attr = createAttributeFixture(Key.key("aincraft", "test_pos"), 1.0);
    assertNotNull(attr.getSentiment(), "Attribute.getSentiment() must never be null");
  }

  @Test
  void testAttributeInstanceBaseValueEquality() {
    Attribute attr = createAttributeFixture(Key.key("aincraft", "test_int"), 1.0);
    AttributeInstance inst = createInstanceFixture(attr, 5.0);
    assertEquals(attr.key(), inst.attribute().key());
    assertEquals(
        5.0,
        inst.baseValue(),
        0.0,
        "AttributeInstance.baseValue() must equal the base value at construction");
  }

  @Test
  void testAttributeInstanceWithoutModifiersReflectsBase() {
    Attribute attr = createAttributeFixture(Key.key("aincraft", "test_iso"), 3.0);
    AttributeInstance inst = createInstanceFixture(attr, 3.0);
    assertEquals(
        3.0,
        inst.value(),
        0.0,
        "An AttributeInstance with no modifiers must report baseValue() as value()");
  }

  @Test
  void testModifierKeyAndIdentityUniqueness() {
    Key k1 = Key.key("aincraft", "mod_a");
    Key k2 = Key.key("aincraft", "mod_b");
    AttributeModifier m1 =
        createModifierFixture(k1, 1.0, AttributeModifier.Operation.ADD_NUMBER, null);
    AttributeModifier m2 =
        createModifierFixture(k2, 2.0, AttributeModifier.Operation.ADD_NUMBER, null);
    assertEquals(k1, m1.key());
    assertEquals(k2, m2.key());
    assertNotEquals(
        m1.key(), m2.key(), "Two distinct modifier keys must remain distinct in the adapter");
  }

  @Test
  void testModifierNonNullId() {
    AttributeModifier mod =
        createModifierFixture(
            Key.key("aincraft", "mod_id"), 0.5, AttributeModifier.Operation.ADD_NUMBER, null);
    assertNotNull(mod.id(), "AttributeModifier.id() must never be null");
  }

  @Test
  void testAddModifierIncreasesValueByAmount() {
    Attribute attr = createAttributeFixture(Key.key("aincraft", "test_add"), 0.0);
    AttributeInstance inst = createInstanceFixture(attr, 0.0);
    AttributeModifier mod =
        createModifierFixture(
            Key.key("aincraft", "add_5"), 5.0, AttributeModifier.Operation.ADD_NUMBER, null);
    inst.addModifier(mod);
    assertEquals(
        5.0, inst.value(), 0.0, "ADD_NUMBER modifier must increase effective value by its amount");
  }

  @Test
  void testSlotGroupModifierRoundTrip() {
    Attribute attr = createAttributeFixture(Key.key("aincraft", "test_sg"), 0.0);
    AttributeInstance inst = createInstanceFixture(attr, 0.0);
    EquipmentSlotGroup group =
        new EquipmentSlotGroup() {
          @Override
          public boolean test(@NotNull EquipmentSlot s) {
            return true;
          }

          @Override
          public EquipmentSlot example() {
            return null;
          }

          @Override
          public @NotNull String name() {
            return "any";
          }
        };
    AttributeModifier mod =
        createSlotGroupModifierFixture(
            Key.key("aincraft", "sg_mod"), 2.0, AttributeModifier.Operation.ADD_NUMBER, group);
    inst.addModifier(mod);
    assertEquals(
        2.0,
        inst.value(),
        0.0,
        "A slot-group modifier must contribute its amount to the effective value");
    assertNotNull(mod.slotGroup(), "A slot-group modifier must report its slotGroup");
    assertEquals(
        "any", mod.slotGroup().name(), "slotGroup().name() must round-trip through the adapter");
  }
}
