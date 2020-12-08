select
 c.id,
 typ.name as "car_type",
 brand.name as "brand",
 c.model as "model",
 c.purchase_time as "purchase_time"
from car c
left join car_type typ on c.car_type_id = typ.id
left join brand on c.brand_id = brand.id