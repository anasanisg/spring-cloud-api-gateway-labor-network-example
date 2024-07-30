package com.tooltracker.backend.walltools.enums;

/*
 * [1] IN_PROCESSING: The user is in the process of selecting tools. (This type can be associated with one movement within movement_table.)
 * [2] IN_USE: The user is using one or more tools and is currently using them. (this type can be associated with one movement per user) the use can't have more than one in_use  movement
 * [3] RETURNED: The user has return all tools that he taken (it's for archive completed movement) the user can have multiple movement with this type at the same time 
 */

public enum MovementStatus {
    IN_PROCESSING, IN_USE, RETURNED
}
