# Archived UI Components

This directory contains UI components that have been archived and are no longer actively used in the application.

## OrderDetailScreen (Archived on Oct 22, 2025)

**Reason for archiving:**
- The order detail screen was replaced by a new flow that navigates directly to product selection for editing orders
- Orders in PENDING status now navigate to `edit/{orderId}/select-products` instead of showing a detail screen
- The customer management module has its own version of OrderDetailScreen that is still in use

**Original location:**
- `presentation/salesforce/screens/orders/detail/`

**Files archived:**
- `OrderDetailScreen.kt` - The composable UI component
- `OrderDetailViewModel.kt` - The view model
- `OrderDetailState.kt` - The state data class

**Note:**
These files are kept for reference and may be restored or adapted if a read-only detail view is needed in the future.
The current implementation focuses on direct editing rather than viewing order details.
