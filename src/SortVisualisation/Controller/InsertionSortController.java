package SortVisualisation.Controller;

import SortVisualisation.Model.ChartDataManager;
import SortVisualisation.Model.Pointer;
import SortVisualisation.Model.RandomGen;
import SortVisualisation.Model.Sorting.AbstractSort;
import SortVisualisation.Model.Sorting.BubbleSort;
import SortVisualisation.Model.Sorting.InsertionSort;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

/**
 * Created by peterzen on 2017-02-28.
 * Modified by femkeh on 2017-05-03.
 * Part of the big-java-assignment-sorting project.
 */
public class InsertionSortController {
    @FXML
    private Slider sldrDelay;
    @FXML
    private TextField fldDelay;
    @FXML
    private Button btnOneStep;
    @FXML
    private Button btnStartPause;
    @FXML
    private Button btnInput;
    @FXML
    private BarChart barChart;
    @FXML
    private TextField fldInput;

    private ChartDataManager chartData;
    private int[] unsortedIntegers;
    private AbstractSort sorter;
    private InsertionSortController.SortingThread sortingThread;

    public InsertionSortController() {
    }

    public void updateDelaySlider(Event event) {
        if (this.textFieldHasValidInt(fldDelay)) {
            sldrDelay.setValue(Double.parseDouble(fldDelay.getText()));
        }
    }

    public void updateDelayField(Event event) {
        Double value = sldrDelay.getValue();
        fldDelay.setText(value.intValue() + "");
    }

    public void visualiseOneSortingStep(ActionEvent actionEvent) {
        // perform one sorting step on our unsorted array
        unsortedIntegers = sorter.sortOneStep();
        updateBarChartData();

        // set styling for bars that are being compared
        updateBarChartSelected();

        System.out.println(btnOneStep.getText() + " == Done");
    }

    public void startOrPauseSorting(ActionEvent actionEvent) {
        if (btnStartPause.getText().equals("Start")) {
            btnStartPause.setText("Pause");

            // start the SortingThread
            if (sortingThread == null) {
                sortingThread = new InsertionSortController.SortingThread();
                sortingThread.start();
            } else {
                sortingThread.running = true;
            }
        } else {
            btnStartPause.setText("Start");

            // pause the SortingThread
            sortingThread.running = false;
        }

        System.out.println(btnStartPause.getText() + " == Done");
    }

    /**
     * This method is automatically invoked when an fxml file, that points to this
     * Controller, is loaded.
     */
    @SuppressWarnings("unused")
    public void initialize() {
        // Use the ChartDataManager to manage our BarChart data
        this.chartData = new ChartDataManager(barChart);
    }

    public void visualiseInput(ActionEvent actionEvent) {
        // @TODO: fix proper error handling
        if (!textFieldHasValidInt(fldInput)) {
            System.out.println("Error: You have not entered a number for how many bars you would like to see.");
            return;
        }

        // generate the random integers collection
        unsortedIntegers = RandomGen.generateRandomInts(Integer.parseInt(fldInput.getText()));

        // initialize our sorting algorithm
        sorter = new InsertionSort(unsortedIntegers);

        // update the barChart
        this.updateBarChartData();
        chartData.selectNode(0);
        chartData.selectNode(1); // the pointer always starts at 0 (and compares with 1)

        // reset our SortingThread
        sortingThread = null;
        btnStartPause.setText("Start");

        // unlock the sorting buttons
        btnStartPause.disableProperty().setValue(false);
        btnOneStep.disableProperty().setValue(false);

        System.out.println(btnInput.getText() + " == Done");
    }

    private boolean textFieldHasValidInt(TextField fldText) {
        try {
            int i = Integer.parseInt(fldText.getText());
//            System.out.println(i + " is valid");
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Input: " + fldText.getText() + " could not be parsed to an integer");
        }
        return false;
    }

    private void updateBarChartData() {
        chartData.updateDataSet(unsortedIntegers);
        chartData.load();
        chartData.styleChartData("BarChart-default");
    }

    private void updateBarChartSelected() {
        chartData.clearSelectedNodes();
        Pointer pointer = sorter.getPointer();
        pointer.getIndices().forEach(i -> chartData.selectNode(i));
    }

//    private void updateBarChartInsertSelected() {
//        chartData.clearSelectedNodes();
//        int pointer = sorter.getPointer();
//        chartData.selectNode(pointer);
//        if (pointer+1 <= unsortedIntegers.length-1) { //otherwise index out of bounds at the end when done with sort
//            chartData.selectNode(pointer + 1);
//        }
//        while (sorter.getTemp() < 5) {
//            int temp = sorter.getTemp();
//            chartData.selectNode(temp);
//            if (temp+1 <= unsortedIntegers.length-1) { //otherwise index out of bounds at the end when done with sort
//                chartData.selectNode(temp + 1);
//            }
//        }
//    }

    private int getDelayPerStep() {
        int delay = 100; // defaults to 100ms
        if (textFieldHasValidInt(fldDelay))
            delay = Integer.parseInt(fldDelay.getText());
        return delay;
    }

    private class SortingThread extends Thread {
        private volatile boolean running = true;

        @Override
        public void run() {
            while (!sorter.isFinished()) {
                while (!running) {
                    yield();
                }

                unsortedIntegers = sorter.sortOneStep();
                Platform.runLater(() -> {
                    updateBarChartData();
                    updateBarChartSelected();
                });

                try {
                    Thread.sleep(getDelayPerStep());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
