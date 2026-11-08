import { Client } from "jsr:@db/postgres";

const client = new Client({
    hostname: "127.0.0.1",
    port: 5432,
    user: "postgres",
    password: "farmate",
    database: "farmate_db",
});

await client.connect();
console.log("✅ Connected to PostgreSQL!");

function json(data: unknown, status = 200) {
    return new Response(JSON.stringify(data), {
        status,
        headers: { "Content-Type": "application/json" },
    });
}

console.log("Deno server running on http://localhost:8000");

Deno.serve({ hostname: "0.0.0.0", port: 8000 }, async (req) => {
    const url = new URL(req.url);

console.log("REQUEST:", req.method, url.pathname);

    // Test server
    if (req.method === "GET" && url.pathname === "/") {
        return new Response("FarMate Server is running!");
    }

    // Login API
    if (req.method === "POST" && url.pathname === "/api/login") {
        try {
            const body = await req.json();
            const { username, password } = body;

            const result = await client.queryObject(
                `SELECT id, username, role, status
                 FROM users
                 WHERE username = $1
                 AND password = $2
                 AND status = 'ACTIVE'`,
                [username, password],
            );

            if (result.rows.length > 0) {
                return json({
                    success: true,
                    message: "Login successful",
                    user: result.rows[0],
                });
            }

            return json({ success: false, message: "Invalid username or password" }, 401);
        } catch (error) {
            console.error(error);
            return json({ success: false, message: "Server error" }, 500);
        }
    }

    // Item request API (replaces the Oak router)
    if (req.method === "POST" && url.pathname === "/api/request") {
        try {
            const body = await req.json();
            const { item_name, quantity, unit, farm_area, purpose, employee_id } = body;

            if (!item_name || !(Number(quantity) > 0)) {
                return json({ error: "Item name and quantity are required" }, 400);
            }

            await client.queryArray(
                `INSERT INTO resource_requests (item_name, quantity, unit, farm_area, purpose, employee_id)
                 VALUES ($1, $2, $3, $4, $5, $6)`,
                [item_name, quantity, unit, farm_area || null, purpose || null, employee_id],
            );

            return json({ message: "Request item submitted successfully!" }, 201);
        } catch (error) {
            console.error(error);
            const message = error instanceof Error ? error.message : "Bad request";
            return json({ error: message }, 400);
        }
    }

    // Stock API
    if (req.method === "GET" && url.pathname === "/api/stock") {
        try {
            const result = await client.queryObject(
                `SELECT stock_id AS id,
                        item_name AS name,
                        quantity,
                        unit,
                        (quantity > 0) AS available
                 FROM stock
                 ORDER BY stock_id ASC`,
            );
            return json(result.rows);
        } catch (error) {
            console.error(error);
            return json({ success: false, message: "Server error" }, 500);
        }
    }

// ==========================================
// NEW ADMIN ROUTE 1: GET ALL RESOURCE REQUESTS WITH EMPLOYEE NAME
// ==========================================
if (req.method === "GET" && url.pathname === "/api/admin/requests") {
    try {
        // This executes the SQL JOIN query using your exact database tables
        const result = await client.queryObject<{
            id: number;
            employee_id: number;
            item_name: string;
            quantity: string; // Numeric data type returns as a string representation
            unit: string;
    farm_area: string | null; 
            purpose: string | null;  
            status: string;
            admin_remarks: string | null;
            employee_name: string; // The joined username field
        }>(
            `SELECT 
                rr.id,
                rr.employee_id,
                rr.item_name,
                rr.quantity,
                rr.unit,
   		rr.farm_area,                 
		 rr.purpose, 
                rr.status,
                rr.admin_remarks,
                u.username AS employee_name
             FROM public.resource_requests rr
             LEFT JOIN public.users u ON rr.employee_id = u.id
             ORDER BY rr.id DESC`,
        );

        // Sends back the array of unified objects to your Android application
        return json(result.rows);
    } catch (error) {
        console.error("Database query failed:", error);
        return json({ success: false, message: "Server error fetching admin requests" }, 500);
    }
}
	
      // ==========================================
    // NEW ADMIN ROUTE 2: UPDATE REQUEST STATUS (PATCH)
    // ==========================================
const statusMatch = url.pathname.match(
    /^\/api\/admin\/requests\/(\d+)\/status$/
);    if (req.method === "PATCH" && statusMatch) {
        try {
           
            const requestId = parseInt(statusMatch[1]); 
            const body = await req.json();
            const { status, admin_remarks } = body; 

              const statusUpper = String(status ?? "").toUpperCase().trim();

// Convert Android status values into database-compatible values
// Convert Android status values into database-compatible values
const statusMap: Record<string, string> = {
    "PENDING": "PENDING",
    "APPROVED": "APPROVED",
    "DECLINED": "REJECTED",
    "REJECTED": "REJECTED",
    "PREPARING": "PREPARING",
    "RELEASED": "RELEASED",
    "COMPLETED": "COMPLETED"
};

const databaseStatusValue = statusMap[statusUpper];

// Validate status
if (!databaseStatusValue) {
    return json({
        success: false,
        message: "Invalid request status: " + statusUpper
    }, 400);
}

// Require remarks when declining/rejecting
if (
    databaseStatusValue === "REJECTED" &&
    (!admin_remarks || admin_remarks.trim() === "")
) {
    return json({
        success: false,
        message: "A note is required when declining a request."
    }, 400);
}
            // Update row status and store remarks inside resource_requests table
            const result = await client.queryObject(
                `UPDATE public.resource_requests 
                 SET status = $1, admin_remarks = $2 
                 WHERE id = $3 
                 RETURNING *`,
                [databaseStatusValue, admin_remarks || null, requestId],
            );
            if (result.rows.length === 0) {
                return json({ success: false, message: "Request item entry not found." }, 404);
            }

            return json({
                success: true,
                message: `Request successfully marked as ${status}`
            });
        } catch (error) {
            // This logs the exact issue in your Deno terminal if it fails!
            console.error("⛔ PATCH Status Error:", error);
            return json({ success: false, message: "Server error changing status" }, 500);
        }
    }

// ==========================================
// EMPLOYEE: GET OWN RESOURCE REQUESTS
// ==========================================
if (req.method === "GET" && url.pathname === "/api/employee/requests") {
    try {
        const employeeId = Number(url.searchParams.get("employee_id"));

        if (!Number.isInteger(employeeId) || employeeId <= 0) {
            return json({
                success: false,
                message: "Valid employee ID is required"
            }, 400);
        }

        const result = await client.queryObject(`
            SELECT
                id,
                employee_id,
                item_name,
                quantity,
                unit,
                farm_area,
                purpose,
                status,
                admin_remarks
            FROM public.resource_requests
            WHERE employee_id = $1
            ORDER BY id DESC
        `, [employeeId]);

        return json(result.rows);

    } catch (error) {
        console.error("Employee request query failed:", error);

        return json({
            success: false,
            message: "Error retrieving employee requests"
        }, 500);
    }
}



    // Unknown endpoint
    return json({ success: false, message: "Endpoint not found" }, 404);
});



