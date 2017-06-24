import os
from os.path import join

dir = "../recipes"
test = os.listdir(dir)

for item in test:
    if item.endswith(".json"):
        os.remove(join(dir, item))

bases = ["stone", "cobblestone", "stonebrick", "planks", "crafting_table", "gravel", "noteblock", "sandstone",
         "gold_block", "iron_block", "brick_block", "mossy_cobblestone", "obsidian", "diamond_block",
         "emerald_block", "lapis_block"]

data0 = ",\n      \"data\": 0"
data = [data0, "", data0, "", "", "", "", data0,
        "", "", "", "", "", "", "", ""]

planks = '''    "*": [
      {
        "item": "minecraft:planks",
        "data": 0
      },
      {
        "item": "minecraft:planks",
        "data": 1
      },
      {
        "item": "minecraft:planks",
        "data": 2
      },
      {
        "item": "minecraft:planks",
        "data": 3
      },
      {
        "item": "minecraft:planks",
        "data": 4
      },
      {
        "item": "minecraft:planks",
        "data": 5
      }
    ]'''

recipe_template = '''
{{
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "XXX",
    "XGX",
    "*R*"
  ],
  "key": {{
    "X":{{
      "item": "{0}"
    }},
    "G": {{
      "item": "{1}"
    }},
    "R": {{
      "item": "{2}"
    }},
    {3}
  }},
  "result": {{
    "item": "{4}",
    "data": {5}
  }}
}}
'''

item_template = '''"*": {{
      "item": "{0}"{1}
    }}'''

for i in range(0, len(bases)):
    recipe = recipe_template.format("minecraft:glass", "minecraft:gold_ingot", "minecraft:redstone",
                                    planks if bases[i] == "planks" else item_template.format("minecraft:" + bases[i],
                                                                                             data[i]),
                                    "vending:vendingMachine", i)
    file = open("vending_machine_" + bases[i] + ".json", "w")
    file.write(recipe)
    file.close()

    recipe = recipe_template.format("minecraft:glass", "minecraft:gold_ingot", "minecraft:repeater",
                                    planks if bases[i] == "planks" else item_template.format("minecraft:" + bases[i],
                                                                                             data[i]),
                                    "vending:vendingMachineAdvanced", i)
    file = open("advanced_vending_machine_" + bases[i] + ".json", "w")
    file.write(recipe)
    file.close()

    recipe = recipe_template.format("minecraft:glass", "minecraft:gold_ingot", "minecraft:dispenser",
                                    planks if bases[i] == "planks" else item_template.format("minecraft:" + bases[i],
                                                                                             data[i]),
                                    "vending:vendingMachineMultiple", i)
    file = open("multiple_vending_machine_" + bases[i] + ".json", "w")
    file.write(recipe)
    file.close()
