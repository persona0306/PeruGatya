package peru.sugoi.perugatya;

import hm.moe.pokkedoll.warscore.utils.Item;

public class TradeItem extends Item {
	private final ItemType type;

	public TradeItem(ItemType type, String name, int amount) {
		super(name, amount);
		this.type = type;
	}

	public TradeItem(ItemType type, Item item) {
		this(type, item.name(), item.amount());
	}

	public ItemType getType() {
		return type;
	}

}
