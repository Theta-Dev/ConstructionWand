# Testing item containers
- Install the toolbelt mod (https://www.curseforge.com/minecraft/mc-mods/tool-belt)
- Place a chest on top of a command block and enter this command:
- Get a toolbeld with 2 stacks of diamond blocks in it

The reason for this hack is that the toolbelt mod gets updated very quickly and is available for almost every
MC version supported by ConstructionWand.

```
data merge block ~ ~1 ~ {Items: [{Slot: 0, id: "toolbelt:belt", Count: 1, tag: {Items: [{Slot: 0, id: "minecraft:diamond_block", Count: 64}, {Slot: 1, id: "minecraft:diamond_block", Count: 64}]}}]}
```
