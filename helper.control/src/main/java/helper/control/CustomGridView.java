package helper.control;


import helper.keyboard.Keyboard;
import impl.org.controlsfx.ReflectionUtils;
import impl.org.controlsfx.skin.GridViewSkin;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Skin;
import javafx.scene.control.skin.CellSkinBase;
import javafx.scene.control.skin.VirtualContainerBase;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class CustomGridView<T> extends GridView<T> {
    private CustomGridViewSkin gvContactsSkin;
    private Consumer<T> _UpdateSelection;

    protected Skin<? extends T> createDefaultSkin() {
        if (gvContactsSkin == null) {
            gvContactsSkin = new CustomGridViewSkin<T>(this);
        }
        return gvContactsSkin;
    }

    public void setUpdateSelection(Consumer<T> callback) {
        _UpdateSelection = callback;
    }

    public void moveNext(KeyEvent keyEvent, T obj) {
        gvContactsSkin.moveNext(keyEvent, obj);
        requestFocus();
    }

    private class CustomGridViewSkin<E> extends GridViewSkin<E> {
        private int rows;
        private int maxCells;

        private Map<KeyCombination, Pair<Predicate<KeyEvent>, Consumer<E>>> mapNavigation = Map.of(
                Keyboard.COMBINATION_DOWN, new Pair<>(Keyboard.COMBINATION_DOWN::match, obj -> {
                            if (obj == null) {
                                selectFirst();
                                return;
                            }
                            selectDown(obj);
                        }),
                Keyboard.COMBINATION_UP, new Pair<>(Keyboard.COMBINATION_UP::match, obj -> {
                            if (obj != null) {
                                selectUp(obj);
                            }
                        }),
                Keyboard.COMBINATION_LEFT, new Pair<>(Keyboard.COMBINATION_LEFT::match, obj -> {
                            if (obj != null) {
                                selectLeft(obj);
                            }
                        }),
                Keyboard.COMBINATION_RIGHT, new Pair<>(Keyboard.COMBINATION_RIGHT::match, obj -> {
                            if (obj != null) {
                                selectRight(obj);
                            }
                        })
        );

        @SuppressWarnings("rawtypes")
        public CustomGridViewSkin(GridView<E> control) {
            super(control);
//            getVerticalBar();
        }

        @Override
        protected void updateItemCount() {
            super.updateItemCount();
            rows = getItemCount();
            maxCells = computeMaxCellsInRow();
        }

        private ScrollBar getVerticalBar() {
            for (Node child : getVirtualFlow().getChildrenUnmodifiable()) {
                if (child instanceof ScrollBar) {
                    if (((ScrollBar) child).getOrientation() == Orientation.VERTICAL) {
                        return (ScrollBar) child;
                    }
                }
            }
            return null;
        }

        public void moveNext(KeyEvent keyEvent, E obj) {
            for (Map.Entry<KeyCombination, Pair<Predicate<KeyEvent>, Consumer<E>>> entry : this.mapNavigation.entrySet()) {
                Pair<Predicate<KeyEvent>, Consumer<E>> pairProcessEvent = entry.getValue();
                if (pairProcessEvent.getKey().test(keyEvent)) {
                    if (pairProcessEvent.getValue() != null)
                        pairProcessEvent.getValue().accept(obj);
                    keyEvent.consume();
                    break;
                }
            }
        }

        private void scrollTo(ObservableList<T> items, int currIdx) {
            int rowIdx = (int) Math.floor((double)currIdx / (double)maxCells);
            getVirtualFlow().scrollTo(rowIdx);
            if (_UpdateSelection != null)
                _UpdateSelection.accept(items.get(currIdx));
        }

        private void selectFirst() {
            ObservableList items = CustomGridView.this.getItems();
            if (!items.isEmpty())
                scrollTo(items, 0);
        }

        private void selectLeft(E obj) {
            ObservableList items = CustomGridView.this.getItems();
            int currIdx = items.indexOf(obj);
            if (currIdx > 0) {
                int prevIdx = currIdx - 1;
                if (prevIdx >= 0) {
                    scrollTo(items, prevIdx);
                }
            }
        }

        private void selectRight(E obj) {
            ObservableList items = CustomGridView.this.getItems();
            int currIdx = items.indexOf(obj);
            int size = items.size();
            if (currIdx < size - 1) {
                int nextIdx = currIdx + 1;
                if (nextIdx < size) {
                    scrollTo(items, nextIdx);
                }
            }
        }

        private void selectUp(E obj) {
            ObservableList items = CustomGridView.this.getItems();
            int currIdx = items.indexOf(obj);
            if (currIdx > 0) {
                int upIdx = currIdx - maxCells;
                if (upIdx >= 0)
                    scrollTo(items, upIdx);
            }
        }

        private void selectDown(E obj) {
            ObservableList items = CustomGridView.this.getItems();
            int currIdx = items.indexOf(obj);
            int size = items.size();
            if (currIdx < size - 1) {
                int downIdx = currIdx + maxCells;
                if (downIdx < size) {
                    scrollTo(items, downIdx);
                }
                else {
                    int rowIdx = (int) Math.floor((double)downIdx / (double)maxCells);
                    if (rowIdx == rows) {
                        downIdx = (maxCells * (rowIdx - 1)) + 1;
                        scrollTo(items, downIdx);
                    }
                }
            }
        }
    }

//    class GridRow<F> extends IndexedCell<F> {
//
//
//        /**************************************************************************
//         *
//         * Constructors
//         *
//         **************************************************************************/
//
//        /**
//         *
//         */
//        public GridRow() {
//            super();
//            getStyleClass().add("grid-row"); //$NON-NLS-1$
//
//            // we need to do this (or something similar) to allow for mouse wheel
//            // scrolling, as the GridRow has to report that it is non-empty (which
//            // is the second argument going into updateItem).
//            indexProperty().addListener(new InvalidationListener() {
//                @Override public void invalidated(Observable observable) {
//                    updateItem(null, getIndex() == -1);
//                }
//            });
//        }
//
//        /**
//         * {@inheritDoc}
//         */
//        @Override protected Skin<?> createDefaultSkin() {
//            return new CustomGridRowSkin<>(this);
//        }
//
//
//
//        /**************************************************************************
//         *
//         * Properties
//         *
//         **************************************************************************/
//
//        /**
//         * The {@link GridView} that this GridRow exists within.
//         */
//        public SimpleObjectProperty<GridView<F>> gridViewProperty() {
//            return gridView;
//        }
//        private final SimpleObjectProperty<GridView<F>> gridView =
//                new SimpleObjectProperty<>(this, "gridView"); //$NON-NLS-1$
//
//        /**
//         * Sets the {@link GridView} that this GridRow exists within.
//         */
//        public final void updateGridView(GridView<F> gridView) {
//            this.gridView.set(gridView);
//        }
//
//        /**
//         * Returns the {@link GridView} that this GridRow exists within.
//         */
//        public GridView<F> getGridView() {
//            return gridView.get();
//        }
//    }
//
//    class CustomGridRowSkin<V> extends CellSkinBase<GridRow<V>> {
//
//        public CustomGridRowSkin(GridRow<V> control) {
//            super(control);
//
//            // Remove any children before creating cells (by default a LabeledText exist and we don't need it)
//            getChildren().clear();
//            updateCells();
//
//            registerChangeListener(getSkinnable().indexProperty(), e -> updateCells());
//            registerChangeListener(getSkinnable().widthProperty(), e -> updateCells());
//            registerChangeListener(getSkinnable().heightProperty(), e -> updateCells());
//        }
//
//        /**
//         *  Returns a cell element at a desired index
//         *  @param index The index of the wanted cell element
//         *  @return Cell element if exist else null
//         */
//        @SuppressWarnings("unchecked")
//        public GridCell<V> getCellAtIndex(int index) {
//            if( index < getChildren().size() ) {
//                return (GridCell<V>)getChildren().get(index);
//            }
//            return null;
//        }
//
//        /**
//         *  Update all cells
//         *  <p>Cells are only created when needed and re-used when possible.</p>
//         */
//        public void updateCells() {
//            int rowIndex = getSkinnable().getIndex();
//            if (rowIndex >= 0) {
//                GridView<V> gridView = getSkinnable().getGridView();
//                int maxCellsInRow = ((CustomGridViewSkin<?>)gridView.getSkin()).computeMaxCellsInRow();
//                int totalCellsInGrid = gridView.getItems().size();
//                int startCellIndex = rowIndex * maxCellsInRow;
//                int endCellIndex = startCellIndex + maxCellsInRow - 1;
//                int cacheIndex = 0;
//
//                for (int cellIndex = startCellIndex; cellIndex <= endCellIndex; cellIndex++, cacheIndex++) {
//                    if (cellIndex < totalCellsInGrid) {
//                        // Check if we can re-use a cell at this index or create a new one
//                        GridCell<V> cell = getCellAtIndex(cacheIndex);
//                        if( cell == null ) {
//                            cell = createCell();
//                            getChildren().add(cell);
//                        }
//                        cell.updateIndex(-1);
//                        cell.updateIndex(cellIndex);
//                    }
//                    // we are going out of bounds -> exist the loop
//                    else { break; }
//                }
//
//                // In case we are re-using a row that previously had more cells than
//                // this one, we need to remove the extra cells that remain
//                getChildren().remove(cacheIndex, getChildren().size());
//            }
//        }
//
//        private GridCell<V> createCell() {
//            GridView<V> gridView = getSkinnable().gridViewProperty().get();
//            GridCell<V> cell;
//            if (gridView.getCellFactory() != null) {
//                cell = gridView.getCellFactory().call(gridView);
//            } else {
//                cell = createDefaultCellImpl();
//            }
//            cell.updateGridView(gridView);
//            return cell;
//        }
//
//        private GridCell<V> createDefaultCellImpl() {
//            return new GridCell<V>() {
//                @Override protected void updateItem(V item, boolean empty) {
//                    super.updateItem(item, empty);
//                    if(empty) {
//                        setText(""); //$NON-NLS-1$
//                    } else {
//                        setText(item.toString());
//                    }
//                }
//            };
//        }
//
//        @Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
//            return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
//        }
//
//        @Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
//            return Double.MAX_VALUE;
//        }
//
//        @Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
//            GridView<V> gv = getSkinnable().gridViewProperty().get();
//            return gv.getCellHeight() + gv.getVerticalCellSpacing() * 2;
//        }
//
//        @Override protected void layoutChildren(double x, double y, double w, double h) {
////        double currentWidth = getSkinnable().getWidth();
//            double cellWidth = getSkinnable().gridViewProperty().get().getCellWidth();
//            double cellHeight = getSkinnable().gridViewProperty().get().getCellHeight();
//            double horizontalCellSpacing = getSkinnable().gridViewProperty().get().getHorizontalCellSpacing();
//            double verticalCellSpacing = getSkinnable().gridViewProperty().get().getVerticalCellSpacing();
//
//            double xPos = 0;
//            double yPos = 0;
//
//            // This has been commented out as I removed the API from GridView until
//            // a use case was created.
////        HPos currentHorizontalAlignment = getSkinnable().gridViewProperty().get().getHorizontalAlignment();
////        if (currentHorizontalAlignment != null) {
////            if (currentHorizontalAlignment.equals(HPos.CENTER)) {
////                xPos = (currentWidth % computeCellWidth()) / 2;
////            } else if (currentHorizontalAlignment.equals(HPos.RIGHT)) {
////                xPos = currentWidth % computeCellWidth();
////            }
////        }
//
//            for (Node child : getChildren()) {
//                child.relocate(xPos + horizontalCellSpacing, yPos + verticalCellSpacing);
//                child.resize(cellWidth, cellHeight);
//                xPos = xPos + horizontalCellSpacing + cellWidth + horizontalCellSpacing;
//            }
//        }
//    }
}
