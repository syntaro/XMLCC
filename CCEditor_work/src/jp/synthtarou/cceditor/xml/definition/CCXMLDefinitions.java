/*
 * Copyright 2023 Syntarou YOSHIDA.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jp.synthtarou.cceditor.xml.definition;

import jp.synthtarou.cceditor.view.CCValueRule;

/**
 *
 * @author Syntarou YOSHIDA
 */
public class CCXMLDefinitions {

    static CCXMLDefinitions _instance = new CCXMLDefinitions();
    
    public static CCXMLDefinitions getInstance() {
        return _instance;
    }

    public CCXMLDefTag getModuleData() { 
        return moduleData;
    }

    public CCXMLDefTag getInstrumentList() {
        return instrumentList;
    }

    public CCXMLDefTag getDrumSetList() { 
        return drumSetList;
    }

    public CCXMLDefTag getControlChangeMacroList() { 
        return controlChangeMacroList;
    }

    public CCXMLDefTag getTemplateList() { 
        return templateList;
    }

    public CCXMLDefTag getDefaultData() { 
        return defaultData;
    }

    final CCXMLDefTag moduleData;
    final CCXMLDefTag moduleData_controlCangeDefault;
    final CCXMLDefTag moduleData_exclusiveDefault;
    final CCXMLDefTag moduleData_progDefault;
    final CCXMLDefTag moduleData_rhythmDefault;

    final CCXMLDefTag instrumentList;
    final CCXMLDefTag instrumentList_map;
    final CCXMLDefTag instrumentList_pc;
    final CCXMLDefTag instrumentList_bank;

    final CCXMLDefTag drumSetList;
    final CCXMLDefTag drumSetList_map;
    final CCXMLDefTag drumSetList_pc;
    final CCXMLDefTag drumSetList_bank;
    final CCXMLDefTag drumSetList_tone;

    final CCXMLDefTag controlChangeMacroList;
    final CCXMLDefTag controlChangeMacroList_folder;
    final CCXMLDefTag controlChangeMacroList_folderlink;
    final CCXMLDefTag controlChangeMacroList_ccm;
    final CCXMLDefTag controlChangeMacroList_ccm_value;
    final CCXMLDefTag controlChangeMacroList_ccm_value_entry;
    final CCXMLDefTag controlChangeMacroList_ccm_gate;
    final CCXMLDefTag controlChangeMacroList_ccm_gate_entry;
    final CCXMLDefTag controlChangeMacroList_ccm_memo;
    final CCXMLDefTag controlChangeMacroList_ccm_data;
    final CCXMLDefTag controlChangeMacroList_ccmLink;
    final CCXMLDefTag controlChangeMacroList_table;
    final CCXMLDefTag controlChangeMacroList_table_entry;

    final CCXMLDefTag templateList;
    final CCXMLDefTag templateList_folder;
    final CCXMLDefTag templateList_template;
    final CCXMLDefTag templateList_template_memo;
    final CCXMLDefTag templateList_template_cc;
    final CCXMLDefTag templateList_template_pc;
    final CCXMLDefTag templateList_template_comment;

    final CCXMLDefTag defaultData;
    final CCXMLDefTag defaultData_mark;
    final CCXMLDefTag defaultData_track;
    final CCXMLDefTag defaultData_track_mark;
    final CCXMLDefTag defaultData_track_tempo;
    final CCXMLDefTag defaultData_track_timeSignature;
    final CCXMLDefTag defaultData_track_keySignature;
    final CCXMLDefTag defaultData_track_CC;
    final CCXMLDefTag defaultData_track_PC;
    final CCXMLDefTag defaultData_track_Comment;
    final CCXMLDefTag defaultData_track_Template;
    final CCXMLDefTag defaultData_track_EOT;

    protected CCXMLDefinitions() {
        moduleData = new CCXMLDefTag("ModuleData");

        /* ModuleData */
        moduleData.readyForAttributeMust("Name", CCValueRule.valueRuleText); 
        moduleData.readyForAttribute("Folder", "", CCValueRule.valueRuleText);
        moduleData.readyForAttribute("Priority", "100", CCValueRule.valueRulePlusMinus);
        moduleData.readyForAttribute("FileCreator", "", CCValueRule.valueRuleText);
        moduleData.readyForAttribute("FileVersion", "", CCValueRule.valueRuleText);
        moduleData.readyForAttribute("WebSite", "", CCValueRule.valueRuleText);

        /* ModuleData/RhythmTrackDefault */
        moduleData_rhythmDefault = new CCXMLDefTag("RhythmTrackDefault");
        moduleData.addChild(moduleData_rhythmDefault);
        moduleData_rhythmDefault.readyForAttributeMust("Gate", CCValueRule.valueRulePlusMinus);

        /* ModuleData/ExclusiveEventDefault */
        moduleData_exclusiveDefault = new CCXMLDefTag("ExclusiveEventDefault");
        moduleData.addChild(moduleData_exclusiveDefault);
        moduleData_exclusiveDefault.readyForAttributeMust("Data", CCValueRule.valueRuleText);

        /* ModuleData/ProgramChangeEventPropertyDlg */
        moduleData_progDefault = new CCXMLDefTag("ProgramChangeEventPropertyDlg");
        moduleData.addChild(moduleData_progDefault);
        moduleData_progDefault.readyForAttribute("AutoPreviewDelay", "0", CCValueRule.valueRule14bit);

        /* ModuleData/ControlChangeEventDefault */
        moduleData_controlCangeDefault = new CCXMLDefTag("ControlChangeEventDefault");
        moduleData.addChild(moduleData_controlCangeDefault);
        moduleData_controlCangeDefault.readyForAttribute("ID", "", CCValueRule.valueRulePlusMinus);

        /* ModuleData/InstrumentList */

        instrumentList = new CCXMLDefTag("InstrumentList");
        moduleData.addChild(instrumentList);

        /* ModuleData/InstrumentList/Map */
        instrumentList_map = new CCXMLDefTag("Map");
        instrumentList.addChild(instrumentList_map);
        instrumentList_map.readyForAttributeMust("Name", CCValueRule.valueRuleText);

        /* ModuleData/InstrumentList/Map/PC */
        instrumentList_pc = new CCXMLDefTag("PC");
        instrumentList_map.addChild(instrumentList_pc);

        instrumentList_pc.readyForAttributeMust("Name", CCValueRule.valueRuleText);
        instrumentList_pc.readyForAttributeMust("PC", CCValueRule.valueRule7bit);

        /* ModuleData/InstrumentList/Map/PC/Bank */
        instrumentList_bank = new CCXMLDefTag("Bank");
        instrumentList_pc.addChild(instrumentList_bank);

        instrumentList_bank.readyForAttributeMust("Name", CCValueRule.valueRuleText);
        instrumentList_bank.readyForAttribute("LSB", "", CCValueRule.valueRule7bit);
        instrumentList_bank.readyForAttribute("MSB", "", CCValueRule.valueRule7bit);

        /* ModuleData/DrumSetList */
        drumSetList = new CCXMLDefTag("DrumSetList");
        moduleData.addChild(drumSetList);

        /* ModuleData/DrumSetList/Map */
        drumSetList_map = new CCXMLDefTag("Map");
        drumSetList.addChild(drumSetList_map);
        drumSetList_map.readyForAttributeMust("Name", CCValueRule.valueRuleText);

        /* ModuleData/DrumSetList/Map/PC */
        drumSetList_pc = new CCXMLDefTag("PC");
        drumSetList_map.addChild(drumSetList_pc);
        drumSetList_pc.readyForAttributeMust("Name", CCValueRule.valueRuleText);
        drumSetList_pc.readyForAttributeMust("PC", CCValueRule.valueRule7bit);

        /* ModuleData/DrumSetList/Map/PC/Bank */
        drumSetList_bank = new CCXMLDefTag("Bank");
        drumSetList_pc.addChild(drumSetList_bank);
        drumSetList_bank.readyForAttributeMust("Name", CCValueRule.valueRuleText);
        drumSetList_bank.readyForAttribute("LSB", "", CCValueRule.valueRule7bit);
        drumSetList_bank.readyForAttribute("MSB", "", CCValueRule.valueRule7bit);

        /* ModuleData/DrumSetList/Map/PC/Bank/Tone */
        drumSetList_tone = new CCXMLDefTag("Tone");
        drumSetList_bank.addChild(drumSetList_tone);
        drumSetList_tone.readyForAttributeMust("Name", CCValueRule.valueRuleText);
        drumSetList_tone.readyForAttributeMust("Key", CCValueRule.valueRule7bit);

        /* ModuleData/ControlChangeMacroList */
        controlChangeMacroList = new CCXMLDefTag("ControlChangeMacroList");
        moduleData.addChild(controlChangeMacroList);

        /* ModuleData/ControlChangeMacroList/Folder */
        controlChangeMacroList_folder = new CCXMLDefTag("Folder");
        controlChangeMacroList.addChild(controlChangeMacroList_folder);
        controlChangeMacroList_folder.addChild(controlChangeMacroList_folder);

        controlChangeMacroList_folder.readyForAttributeMust("Name", CCValueRule.valueRuleText);
        controlChangeMacroList_folder.readyForAttribute("ID", "-1", CCValueRule.valueRulePlusMinus);

        /* ModuleData/ControlChangeMacroList/CCM */
        controlChangeMacroList_ccm = new CCXMLDefTag("CCM");
        controlChangeMacroList.addChild(controlChangeMacroList_ccm);

        controlChangeMacroList_ccm.readyForAttributeMust("ID", CCValueRule.valueRulePlusMinus);
        controlChangeMacroList_ccm.readyForAttributeMust("Name", CCValueRule.valueRuleText);
        controlChangeMacroList_ccm.readyForAttribute("Color", "#000000", CCValueRule.valueRuleColorFormat);
        controlChangeMacroList_ccm.readyForAttribute("Sync", "", CCValueRule.valueRuleSyncOR);
        controlChangeMacroList_ccm.readyForAttribute("MuteSync", "", CCValueRule.valueRule1bit);

        /* ModuleData/ControlChangeMacroList/CCM/Value */
        controlChangeMacroList_ccm_value = new CCXMLDefTag("Value");
        controlChangeMacroList_ccm.addChild(controlChangeMacroList_ccm_value);

        controlChangeMacroList_ccm_value.readyForAttribute("Default", "0", CCValueRule.valueRulePlusMinus);
        controlChangeMacroList_ccm_value.readyForAttribute("Min", "0", CCValueRule.valueRulePlusMinus);
        controlChangeMacroList_ccm_value.readyForAttribute("Max", "127", CCValueRule.valueRulePlusMinus);
        controlChangeMacroList_ccm_value.readyForAttribute("Offset", "0", CCValueRule.valueRulePlusMinus);
        controlChangeMacroList_ccm_value.readyForAttribute("Name", "", CCValueRule.valueRuleText);
        controlChangeMacroList_ccm_value.readyForAttribute("Type", "", CCValueRule.valueRuleKeyOR);
        controlChangeMacroList_ccm_value.readyForAttribute("TableID", "", CCValueRule.valueRulePlusMinus);

        /* ModuleData/ControlChangeMacroList/CCM/Value/Entry */
        controlChangeMacroList_ccm_value_entry = new CCXMLDefTag("Entry");
        controlChangeMacroList_ccm_value.addChild(controlChangeMacroList_ccm_value_entry);

        controlChangeMacroList_ccm_value_entry.readyForAttributeMust("Label", CCValueRule.valueRuleText);
        controlChangeMacroList_ccm_value_entry.readyForAttributeMust("Value", CCValueRule.valueRuleText);

        /* ModuleData/ControlChangeMacroList/CCM/Gate */
        controlChangeMacroList_ccm_gate = new CCXMLDefTag("Gate");
        controlChangeMacroList_ccm.addChild(controlChangeMacroList_ccm_gate);

        controlChangeMacroList_ccm_gate.readyForAttribute("Default", "0", CCValueRule.valueRulePlusMinus);
        controlChangeMacroList_ccm_gate.readyForAttribute("Min", "0", CCValueRule.valueRulePlusMinus);
        controlChangeMacroList_ccm_gate.readyForAttribute("Max", "127", CCValueRule.valueRulePlusMinus);
        controlChangeMacroList_ccm_gate.readyForAttribute("Offset", "0", CCValueRule.valueRulePlusMinus);
        controlChangeMacroList_ccm_gate.readyForAttribute("Name", "", CCValueRule.valueRuleText);
        controlChangeMacroList_ccm_gate.readyForAttribute("Type", "", CCValueRule.valueRuleKeyOR);
        controlChangeMacroList_ccm_gate.readyForAttribute("TableID", "", CCValueRule.valueRulePlusMinus);
        
        /* ModuleData/ControlChangeMacroList/CCM/Gate/Entry */
        controlChangeMacroList_ccm_gate_entry = new CCXMLDefTag("Entry");
        controlChangeMacroList_ccm_gate.addChild(controlChangeMacroList_ccm_gate_entry);

        controlChangeMacroList_ccm_gate_entry.readyForAttributeMust("Label", CCValueRule.valueRuleText);
        controlChangeMacroList_ccm_gate_entry.readyForAttributeMust("Value", CCValueRule.valueRule14bit);
        
        /* ModuleData/ControlChangeMacroList/CCM/Memo */
        controlChangeMacroList_ccm_memo = new CCXMLDefTag("Memo");
        controlChangeMacroList_ccm_memo.readyForText(true);
        controlChangeMacroList_ccm.addChild(controlChangeMacroList_ccm_memo);
        controlChangeMacroList_folder.addChild(controlChangeMacroList_ccm_memo);
        
        /* ModuleData/ControlChangeMacroList/CCM/Data */
        controlChangeMacroList_ccm_data = new CCXMLDefTag("Data");
        //controlChangeMacroList_ccm_data.readyForAttribute("Value", ""); //Undocumented
        controlChangeMacroList_ccm_data.readyForText(true);
        controlChangeMacroList_ccm.addChild(controlChangeMacroList_ccm_data);

        /* ModuleData/ControlChangeMacroList/CCMLink */
        controlChangeMacroList_ccmLink = new CCXMLDefTag("CCMLink");
        controlChangeMacroList.addChild(controlChangeMacroList_ccmLink);

        controlChangeMacroList_ccmLink.readyForAttributeMust("Name", CCValueRule.valueRuleText);
        controlChangeMacroList_ccmLink.readyForAttributeMust("ID", CCValueRule.valueRulePlusMinus);
        controlChangeMacroList_ccmLink.readyForAttribute("Value", "", CCValueRule.valueRule14bit);
        controlChangeMacroList_ccmLink.readyForAttribute("Gate", "", CCValueRule.valueRule14bit);

        /* ModuleData/ControlChangeMacroList/FolderList */
        controlChangeMacroList_folderlink = new CCXMLDefTag("FolderLink");
        controlChangeMacroList.addChild(controlChangeMacroList_folderlink);
        controlChangeMacroList_folder.addChild(controlChangeMacroList_folderlink);

        controlChangeMacroList_folderlink.readyForAttributeMust("Name", CCValueRule.valueRuleText);
        controlChangeMacroList_folderlink.readyForAttributeMust("ID", CCValueRule.valueRulePlusMinus);
        controlChangeMacroList_folderlink.readyForAttribute("Value", "", CCValueRule.valueRule14bit);
        controlChangeMacroList_folderlink.readyForAttribute("Gate", "", CCValueRule.valueRule14bit);
        
        /* ModuleData/ControlChangeMacroList/CCM/Data */
        controlChangeMacroList_ccm_data.readyForText(true);
        controlChangeMacroList_ccm.addChild(controlChangeMacroList_ccm_data);
        
        /* ModuleData/ControlChangeMacroList/Table */
        controlChangeMacroList_table = new CCXMLDefTag("Table");
        controlChangeMacroList.addChild(controlChangeMacroList_table);
        controlChangeMacroList_table.readyForAttributeMust("ID", CCValueRule.valueRulePlusMinus);

        /* ModuleData/ControlChangeMacroList/Table/Entry */
        controlChangeMacroList_table_entry = new CCXMLDefTag("Entry");
        controlChangeMacroList_table.addChild(controlChangeMacroList_table_entry);
        controlChangeMacroList_table_entry.readyForAttributeMust("Label", CCValueRule.valueRuleText);
        controlChangeMacroList_table_entry.readyForAttributeMust("Value", CCValueRule.valueRulePlusMinus);

        /* ModuleData/ControlChangeMacroList/Folder */
        controlChangeMacroList_folder.readyForAttributeMust("Name", CCValueRule.valueRuleText);
        controlChangeMacroList_folder.readyForAttribute("ID", "-1", CCValueRule.valueRulePlusMinus);
        controlChangeMacroList_folder.addChild(controlChangeMacroList_ccm);
        controlChangeMacroList_folder.addChild(controlChangeMacroList_folder);
        controlChangeMacroList_folder.addChild(controlChangeMacroList_ccmLink);
        controlChangeMacroList_folder.addChild(controlChangeMacroList_table);

        /* ModuleData/TemplateList */

        templateList = new CCXMLDefTag("TemplateList");
        moduleData.addChild(templateList);
        
        /* ModuleData/TemplateList/Template */
        templateList_template = new CCXMLDefTag("Template");

        /* ModuleData/TemplateList/Folder */
        templateList_folder = new CCXMLDefTag("Folder");
        templateList.addChild(templateList_template);
        templateList.addChild(templateList_folder);

        templateList_folder.readyForAttributeMust("Name", CCValueRule.valueRuleText);
        templateList_folder.addChild(templateList_template);
        templateList_folder.addChild(templateList_folder);

        templateList_template.readyForAttributeMust("ID", CCValueRule.valueRulePlusMinus);
        templateList_template.readyForAttribute("Name", "", CCValueRule.valueRuleText);

        /* ModuleData/TemplateList/CC */
        templateList_template_cc = new CCXMLDefTag("CC");
        templateList_template.addChild(templateList_template_cc);
        templateList_template_cc.readyForAttributeMust("ID", CCValueRule.valueRulePlusMinus);
        templateList_template_cc.readyForAttribute("Value", "", CCValueRule.valueRulePlusMinus);
        templateList_template_cc.readyForAttribute("Gate", "", CCValueRule.valueRulePlusMinus);

        /* ModuleData/TemplateList/PC */
        templateList_template_pc = new CCXMLDefTag("PC");
        templateList_template.addChild(templateList_template_pc);
        templateList_template_pc.readyForAttribute("PC", "1", CCValueRule.valueRule7bit);
        templateList_template_pc.readyForAttribute("MSB", "", CCValueRule.valueRule7bit);
        templateList_template_pc.readyForAttribute("LSB", "", CCValueRule.valueRule7bit);
        templateList_template_pc.readyForAttribute("Mode", "", CCValueRule.valueRuleTypeDrumOR);

        /* ModuleData/TemplateList/Memo */
        templateList_template_memo = new CCXMLDefTag("Memo");
        templateList_template.addChild(templateList_template_memo);
        templateList_template_memo.readyForText(true);

        /* ModuleData/TemplateList/Comment */
        templateList_template_comment = new CCXMLDefTag("Comment");
        templateList_template.addChild(templateList_template_comment);
        templateList_template_comment.readyForAttribute("Text", "", CCValueRule.valueRuleText);

        /* ModuleData/DefaultData */
        defaultData = new CCXMLDefTag("DefaultData");
        moduleData.addChild(defaultData);

        /* ModuleData/DefaultData/Mark */
        defaultData_mark = new CCXMLDefTag("Mark");
        defaultData.addChild(defaultData_mark);
        defaultData_mark.readyForAttributeMust("ID", CCValueRule.valueRulePlusMinus);
        //defaultData_mark.readyForAttribute("Name", ""); //Undocumented
        //defaultData_mark.readyForAttribute("Meas", ""); //Undocumented

        /* ModuleData/DefaultData/Track */
        defaultData_track = new CCXMLDefTag("Track");
        defaultData.addChild(defaultData_track);
        defaultData_track.readyForAttribute("Name", "", CCValueRule.valueRuleText);
        defaultData_track.readyForAttribute("Ch", "1", CCValueRule.valueRule4bit);
        defaultData_track.readyForAttribute("Mode", "", CCValueRule.valueRuleTypeDrumOR);
        //defaultData_track.readyForAttribute("Current", "1"); //Undocumented

        /* ModuleData/DefaultData/Track/Mark */
        defaultData_track_mark = new CCXMLDefTag("Mark");
        defaultData_track.addChild(defaultData_track_mark);
        defaultData_track_mark.readyForAttribute("Name", "", CCValueRule.valueRuleText);
        defaultData_track_mark.readyForAttribute("Tick", "", CCValueRule.valueRulePlusMinus);
        defaultData_track_mark.readyForAttribute("Step", "", CCValueRule.valueRulePlusMinus);
        //defaultData_track_mark.readyForAttribute("Meas", ""); //Undocumented

        /* ModuleData/DefaultData/Track/Tempo */
        defaultData_track_tempo = new CCXMLDefTag("Tempo");
        defaultData_track.addChild(defaultData_track_tempo);
//        defaultData_track_tempo.readyForAttribute("Tick", "", CCValueRule.valueRulePlusMinus);
        defaultData_track_tempo.readyForAttribute("Tempo", "", CCValueRule.valueRule14bit);
        //defaultData_track_tempo.readyForAttribute("Step", ""); //Undoumented

        /* ModuleData/DefaultData/Track/TimeSignature */
        defaultData_track_timeSignature = new CCXMLDefTag("TimeSignature");
        defaultData_track.addChild(defaultData_track_timeSignature);

        defaultData_track_timeSignature.readyForAttributeMust("TimeSignature", CCValueRule.valueRuleTimeSignature); // 4/4
        defaultData_track_timeSignature.readyForAttribute("Tick", "", CCValueRule.valueRulePlusMinus);
        defaultData_track_timeSignature.readyForAttribute("Step", "", CCValueRule.valueRulePlusMinus);

        /* ModuleData/DefaultData/Track/KeySignature */
        defaultData_track_keySignature = new CCXMLDefTag("KeySignature");
        defaultData_track.addChild(defaultData_track_keySignature);
        defaultData_track_keySignature.readyForAttributeMust("KeySignature", CCValueRule.valueKeySignature);
        defaultData_track_keySignature.readyForAttribute("Tick", "", CCValueRule.valueRulePlusMinus);
        defaultData_track_keySignature.readyForAttribute("Step", "", CCValueRule.valueRulePlusMinus);

        /* ModuleData/DefaultData/Track/CC */
        defaultData_track_CC = new CCXMLDefTag("CC");
        defaultData_track.addChild(defaultData_track_CC);
        defaultData_track_CC.readyForAttributeMust("ID", CCValueRule.valueRulePlusMinus);
        defaultData_track_CC.readyForAttribute("Value", "", CCValueRule.valueRulePlusMinus);
        defaultData_track_CC.readyForAttribute("Gate", "", CCValueRule.valueRulePlusMinus);
        defaultData_track_CC.readyForAttribute("Tick", "", CCValueRule.valueRulePlusMinus);
        defaultData_track_CC.readyForAttribute("Step", "", CCValueRule.valueRulePlusMinus);

        /* ModuleData/DefaultData/Track/PC */
        defaultData_track_PC = new CCXMLDefTag("PC");
        defaultData_track.addChild(defaultData_track_PC);
        defaultData_track_PC.readyForAttribute("PC", "1", CCValueRule.valueRule7bit);
        defaultData_track_PC.readyForAttribute("MSB", "", CCValueRule.valueRule7bit);
        defaultData_track_PC.readyForAttribute("LSB", "", CCValueRule.valueRule7bit);
        defaultData_track_PC.readyForAttribute("Mode", "", CCValueRule.valueRuleTypeDrumOR);
        defaultData_track_PC.readyForAttribute("Tick", "", CCValueRule.valueRulePlusMinus);
        defaultData_track_PC.readyForAttribute("Step", "", CCValueRule.valueRulePlusMinus);

        /* ModuleData/DefaultData/Track/Comment */
        defaultData_track_Comment = new CCXMLDefTag("Comment");
        defaultData_track.addChild(defaultData_track_Comment);
        defaultData_track_Comment.readyForAttribute("Text", "", CCValueRule.valueRuleText);
        defaultData_track_Comment.readyForAttribute("Tick", "", CCValueRule.valueRulePlusMinus);
        defaultData_track_Comment.readyForAttribute("Step", "", CCValueRule.valueRulePlusMinus);
        
        /* ModuleData/DefaultData/Track/Template */
        defaultData_track_Template = new CCXMLDefTag("Template");
        defaultData_track.addChild(defaultData_track_Template);
        defaultData_track_Template.readyForAttribute("ID", "", CCValueRule.valueRulePlusMinus);
        defaultData_track_Template.readyForAttribute("Tick", "", CCValueRule.valueRulePlusMinus);
        defaultData_track_Template.readyForAttribute("Step", "", CCValueRule.valueRulePlusMinus);

        /* ModuleData/DefaultData/Track/EOT */
        defaultData_track_EOT = new CCXMLDefTag("EOT");
        defaultData_track.addChild(defaultData_track_EOT);
        defaultData_track_EOT.readyForAttribute("ID", "", CCValueRule.valueRulePlusMinus);
        defaultData_track_EOT.readyForAttribute("Tick", "", CCValueRule.valueRulePlusMinus);
    }
}
