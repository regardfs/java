package name.abhijitsarkar.algorithms.ooo.restaurant;

import java.util.Map;

public class Menu {
	private Map<Food, Float> itemizedMenu;

	public float getPrice(Food food) {
		return itemizedMenu.get(food);
	}
}
