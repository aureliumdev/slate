package dev.aurelium.slate.position;

import dev.aurelium.slate.inv.content.SlotPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupUtil {

    private final int size;
    private final int startRow;
    private final int startCol;
    private final int endRow;
    private final int endCol;

    public GroupUtil(int size, int startRow, int startCol, int endRow, int endCol) {
        this.size = size;
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
    }

    public List<SlotPos> getCenterSlots() {
        List<SlotPos> result = new ArrayList<>();
        // Calculate the number of rows and columns in the grid
        int rows = endRow - startRow + 1;
        int cols = endCol - startCol + 1;
        // Calculate the number of slots per row
        int slotsPerRow = (int) Math.ceil((double) size / rows);
        // Calculate the offset to center the slots in each row
        int offset = (cols - slotsPerRow) / 2;
        // Fill the slots from left to right, top to bottom
        int count = 0; // Keep track of how many slots are filled
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol + offset; j < startCol + offset + slotsPerRow; j++) {
                if (count < size) { // Check if we have filled enough slots
                    result.add(SlotPos.of(i, j)); // Add the slot position to the result list
                    count++; // Increment the count
                } else {
                    break; // Stop filling slots
                }
            }
        }
        return result;
    }

    public List<SlotPos> getLeftSlots() {
        List<SlotPos> result = new ArrayList<>();
        int count = 0;
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                if (count < size) {
                    result.add(SlotPos.of(i, j));
                    count++;
                } else {
                    break;
                }
            }
        }
        return result;
    }

    public List<SlotPos> getRightSlots() {
        List<SlotPos> result = new ArrayList<>();
        int count = 0;
        for (int i = endRow; i >= startRow; i--) {
            for (int j = endCol; j >= startCol; j--) {
                if (count < size) {
                    result.add(SlotPos.of(i, j));
                    count++;
                } else {
                    break;
                }
            }
        }
        Collections.reverse(result);
        return result;
    }

}
