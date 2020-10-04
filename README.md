# Construction Wand
With a Construction Wand you can place multiple blocks (up to 1024) at once, extending your build on the side you're facing.
Sneak+Right click to activate angel mode which allows you to place a block at the opposite side of the block facing you.
If you concentrate enough, you can even conjure a block in mid air!

![](https://raw.githubusercontent.com/Theta-Dev/ConstructionWand/1.15/images/wands.png)

## Wands
There are basic wands made from stone, iron and diamond and the Infinity wand.
Wand properties can be changed in the config.

| Wand     | Durability  | Max. Blocks | Angel distance |
|----------|-------------|-------------|----------------|
| Stone    | 131         | 9           | No angel mode  |
| Iron     | 250         | 27          | 1              |
| Diamond  | 1561        | 128         | 4              |
| Infinity | Unbreakable | 1024        | 8              |

## Crafting
![](https://raw.githubusercontent.com/Theta-Dev/ConstructionWand/1.15/images/crafting1.png)
![](https://raw.githubusercontent.com/Theta-Dev/ConstructionWand/1.15/images/crafting2.png)
![](https://raw.githubusercontent.com/Theta-Dev/ConstructionWand/1.15/images/crafting3.png)
![](https://raw.githubusercontent.com/Theta-Dev/ConstructionWand/1.15/images/crafting4.png)

## Modes
**Default mode:** Extends your build on the side facing you. Maximum number of blocks depends on wand tier. SHIFT+scroll to change the placement mode (Horizontal, Vertical, North/South, East/West, No lock).

**Angel mode:** Places a block on the opposite side of the block (or row of blocks) you are facing. Maximum distance depends on wand tier. Right click empty space to place a block in midair (similar to angel blocks, hence the name). To do that, you'll need to have the block you want to place in your offhand. You can't place a block in midair if you've fallen more than 10 blocks deep (no easy rescue tool from falling into the void).

You can change the wand mode using the option screen or by SHIFT+Left clicking empty space.

## Options
SHIFT+Right clicking empty space opens the option screen of your wand.

![](https://raw.githubusercontent.com/Theta-Dev/ConstructionWand/1.15/images/options.png)

**Restriction:** If restriction is enabled the wand will only place blocks in one row or column (choose between North/South, East/West on a horizontal plane and Horizontal, Vertical on a vertical plane). If the direction lock is switched off, the wand will extend the entire face of the building it's pointed at. This option has no effect in Angel mode.

**Direction:** If set to "Player" the wand places blocks in the same direction as if they were placed by yourself. Target mode places the blocks in the same direction as their supporting block. See the picture below:

![](https://raw.githubusercontent.com/Theta-Dev/ConstructionWand/1.15/images/placedir.png)

**Replacement:** Enables/disables the replacement of replaceable blocks (Fluids, snow, tallgrass).

**Matching:** Select which blocks are extended by the wand. If set to "EXACT" it will only extend blocks that are exactly the same as the selected block.
"SIMILAR" will treat similar blocks equally (e.g. extend dirt and grass blocks).
"ANY" will extend any block on the face of the building you're looking at.

**Random:** If random mode is enabled, the wand places random blocks from your hotbar. ~~Shamelessly stolen~~ Inspired by the Trowel from Quark.

## Additional features
- If you have shulker boxes in your inventory filled with blocks, the wand can pull them out and place them

- Botania compatibility: The Black Hole Talisman can supply blocks just like shulker boxes can. Having a Rod of the Lands / Rod of the Depths in your inventory will provide you with infinite dirt/cobble at the cost of Mana.

- Holding down SHIFT+CTRL while looking at a blocks will show you the last blocks you placed with a green border around them. SHIFT+CTRL+Right clickking any of them will undo the operation, giving you all the items back.

- Having blocks in your offhand will place them instead of the block you're looking at

- Look at your statisics to see how many blocks you have placed using your wand

- **1.16+ only:** The Infinity Wand won't burn in lava just like netherite gear.

## Contributions and #Hacktoberfest
As #Hacktoberfest now requires repo owners to opt in, I added the tag to this repository.

I'd really appreciate translations. Currently ConstructionWand only has English and German, so if you speak any other language you can help translate the mod and add a new language file under `src/main/resources/assets/constructionwand/lang/`.
