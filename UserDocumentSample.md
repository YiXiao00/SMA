What we should have in a MongoDB User Collection for SmartHome2018
=============================

+ user_collection
  + document_sample
    + **Id**: Generated automatically by database. Unique.
    + **Username**: Store the name of users and can be duplicate.
    + **Password**: At least 6 characters and at most 16.

-------------------
This should work like:
> {
>     "_id": ObjectId("xxx"),
>     "Username": "Abdul",
>     "Password": "abdul2018"
> }



