package controllers.companyData.operations;

import controllers.TableViewInitializer;
import controllers.companyData.AbstractCellController;
import javafx.scene.control.*;
import javafx.stage.Stage;


public abstract class OperationController {
    protected AbstractCellController controller;

    private final static String PATH_TO_OPERATION_STYLES = "css/operationWindow";
    private final static String WRONG_DATA_CHOICE_BOX_PATH = PATH_TO_OPERATION_STYLES + "/choiceBox/wrongDataChoiceBox.css";
    private final static String CHOICE_BOX_STYLE_PATH = PATH_TO_OPERATION_STYLES + "/choiceBox/lightChoiceBoxStyle.css";
    private final static String WRONG_DATA_TEXT_FIELD_PATH = PATH_TO_OPERATION_STYLES + "/textField/wrongDataTextField.css";
    private final static String TEXT_FIELD_STYLE_PATH = PATH_TO_OPERATION_STYLES + "/textField/lightTextFieldStyle.css";
    private final static String WRONG_DATA_COMBO_BOX_PATH = PATH_TO_OPERATION_STYLES + "/comboBox/wrongDataComboBox.css";
    private final static String COMBO_BOX_STYLE_PATH = PATH_TO_OPERATION_STYLES + "/comboBox/lightComboBoxStyle.css";
    private final static String WRONG_DATA_DATE_PICKER_PATH = PATH_TO_OPERATION_STYLES + "/datePicker/wrongDataDatePicker.css";
    private final static String DATE_PICKER_STYLE_PATH = PATH_TO_OPERATION_STYLES + "/datePicker/lightDatePickerStyle.css";

    public void setController(AbstractCellController controller) {
        this.controller = controller;
    }

    public void turnOffStage(Stage currentStage) {
        if (this.controller != null) {
            this.controller.setAdditionalWindowOpened(false);
        }

        currentStage.close();
    }

    protected void setLabelPercentageInfo(Label taxInfoLabel, double taxPercent) {
        if (taxInfoLabel != null) {
            String text = "Według stawki ";
            if (taxPercent == (int) taxPercent) {
                text += (int) taxPercent + "%";
            } else {
                text += taxPercent + "%";
            }
            taxInfoLabel.setText(text);
        }
    }

    protected void refreshControllerList() {
        if (controller != null) {
            TableViewInitializer controller = (TableViewInitializer) this.controller;
            controller.refreshTable();
        }
    }

    protected void setLightWrongDataFieldEffect(Control node) {
        if (node != null) {
            setNodeWrongDataUnderlineEffect(node, true);
            setEffectOnChange(node, true);
        }
    }

    protected void setDarkWrongDataFieldEffect(Control node) {
        if (node != null) {
            setNodeWrongDataUnderlineEffect(node, false);
            setEffectOnChange(node, false);
        }
    }

    private void setEffectOnChange(Control node, boolean themeIsLight) {
        if (node != null) {
            node.setOnMouseClicked(e -> setNoneEffect(node, themeIsLight));
        }
    }

    protected void setNoneEffect(Control node, boolean themeIsLight) {
        if (node != null) {
            String stylePath;
            if (node instanceof ChoiceBox) {
                if (themeIsLight) {
                    stylePath = CHOICE_BOX_STYLE_PATH;
                } else {
                    stylePath = CHOICE_BOX_STYLE_PATH.replace("light", "dark");
                }
            } else if (node instanceof DatePicker) {
                if (themeIsLight) {
                    stylePath = DATE_PICKER_STYLE_PATH;
                } else {
                    stylePath = DATE_PICKER_STYLE_PATH.replace("light", "dark");
                }
            } else if (node instanceof ComboBox) {
                if (themeIsLight) {
                    stylePath = COMBO_BOX_STYLE_PATH;
                } else {
                    stylePath = COMBO_BOX_STYLE_PATH.replace("light", "dark");
                }
            } else {
                if (themeIsLight) {
                    stylePath = TEXT_FIELD_STYLE_PATH;
                } else {
                    stylePath = TEXT_FIELD_STYLE_PATH.replace("light", "dark");
                }
            }
            node.getStylesheets().clear();
            node.getStylesheets().add(stylePath);
        }
    }

    private void setNodeWrongDataUnderlineEffect(Control node, boolean themeIsLight) {
        if (node != null) {
            String wrongDataStylePath;
            if (node instanceof ChoiceBox) {
                if (themeIsLight) {
                    wrongDataStylePath = WRONG_DATA_CHOICE_BOX_PATH;
                } else {
                    wrongDataStylePath = WRONG_DATA_CHOICE_BOX_PATH.replace("ChoiceBox", "DarkChoiceBox");
                }
            } else if (node instanceof ComboBox) {
                if (themeIsLight) {
                    wrongDataStylePath = WRONG_DATA_COMBO_BOX_PATH;
                } else {
                    wrongDataStylePath = WRONG_DATA_COMBO_BOX_PATH.replace("ComboBox", "DarkComboBox");
                }
            } else if (node instanceof DatePicker) {
                if (themeIsLight) {
                    wrongDataStylePath = WRONG_DATA_DATE_PICKER_PATH;
                } else {
                    wrongDataStylePath = WRONG_DATA_DATE_PICKER_PATH.replace("DatePicker", "DarkDatePicker");
                }
            } else {
                if (themeIsLight) {
                    wrongDataStylePath = WRONG_DATA_TEXT_FIELD_PATH;
                } else {
                    wrongDataStylePath = WRONG_DATA_TEXT_FIELD_PATH.replace("TextField", "DarkTextField");
                }
            }
            node.getStylesheets().add(wrongDataStylePath);
        }
    }
}
